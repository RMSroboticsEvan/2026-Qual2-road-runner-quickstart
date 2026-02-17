package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretV2Servo;

/**
 * ⚠️ ACTION REQUIRED: Review and decide whether to delete this file
 *
 * STATUS: UNTRACKED - Never committed by anyone (created Jan 31, 2025)
 *
 * CURRENT REALITY: Robot uses TurretHardware.java (standard Servo), NOT TurretV2Servo
 * - This test is for TurretV2Servo.java (CRServo + external encoder)
 * - Robot actually uses TurretHardware.java (standard Servo "axon")
 * - TeleOp now uses TurretController wrapper (see BaseTeleOp.java:92)
 *
 * WHY THIS EXISTS: Test file for alternative CRServo turret implementation
 *
 * CAN BE DELETED IF:
 * - Not using TurretV2Servo.java
 * - Team is satisfied with current TurretV2.java approach
 * - No plans to test CRServo implementation
 *
 * KEEP IF:
 * - Team wants to test CRServo approach
 * - Need calibration tool for alternative implementation
 * - Want to preserve test code for future reference
 *
 * ---
 *
 * TurretTest for CRServo + External Encoder Version
 *
 * This OpMode demonstrates turret control using:
 * - CRServo for actuation (saves a motor!)
 * - External encoder for position feedback
 * - Angle limiting to prevent cable damage
 * - PD controller for smooth tracking
 */
@TeleOp
public class TurretTestServo extends LinearOpMode {
    private TurretV2Servo turret;
    private Limelight3A limelight;

    @Override
    public void runOpMode() throws InterruptedException {
        turret = new TurretV2Servo(hardwareMap);
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");

        // pipeline 0 is a placeholder, put apriltag pipeline when configured
        limelight.pipelineSwitch(0);

        limelight.start();

        waitForStart();

        // Note: With external encoder, you may need to manually track offset
        // if using incremental encoder instead of absolute encoder
        double encoderOffset = turret.getCurrentPosition();

        double kP = 0.015;
        double kD = 0.003;

        // ANGLE LIMITS (adjust these based on your physical constraints)
        final double MIN_ANGLE = -90.0;  // degrees (left limit)
        final double MAX_ANGLE = 90.0;   // degrees (right limit)
        final double WARNING_ZONE = 10.0; // degrees from limit to start slowing

        // Ticks per degree - update based on your encoder!
        // OPTION 1: REV Through Bore on turret shaft = 22.76 ticks/degree
        // OPTION 2: GoBILDA motor encoder on motor shaft = 3.155 ticks/degree
        final double TICKS_PER_DEGREE = 3.15493055556;  // Adjust for your setup!

        while (opModeIsActive() && !isStopRequested()) {
            LLResult result = limelight.getLatestResult();

            // Get current turret angle from external encoder
            double currentTicks = turret.getCurrentPosition() - encoderOffset;
            double currentAngle = currentTicks / TICKS_PER_DEGREE;

            if (result != null && result.isValid()) {
                double error = result.getTx();

                // deadband
                if (Math.abs(error) < 1.5) {
                    turret.setPower(0);
                    continue;
                }

                double velocity = turret.getVelocity();

                // scale down as we approach target
                double scale = Math.min(1.0, Math.abs(error) / 5.0);

                double power = (kP * error) - (kD * velocity);
                power *= scale;

                // ANGLE LIMIT ENFORCEMENT
                // Check if at hard limits
                if (currentAngle <= MIN_ANGLE && power < 0) {
                    // At left limit, don't allow further left movement
                    power = 0;
                    telemetry.addData("WARNING", "LEFT LIMIT REACHED!");
                } else if (currentAngle >= MAX_ANGLE && power > 0) {
                    // At right limit, don't allow further right movement
                    power = 0;
                    telemetry.addData("WARNING", "RIGHT LIMIT REACHED!");
                } else {
                    // Check if in warning zone
                    double distanceFromMinLimit = currentAngle - MIN_ANGLE;
                    double distanceFromMaxLimit = MAX_ANGLE - currentAngle;

                    if (distanceFromMinLimit < WARNING_ZONE && power < 0) {
                        // Approaching left limit, reduce power
                        double limitScale = distanceFromMinLimit / WARNING_ZONE;
                        power *= limitScale;
                        telemetry.addData("WARNING", "Approaching LEFT limit");
                    } else if (distanceFromMaxLimit < WARNING_ZONE && power > 0) {
                        // Approaching right limit, reduce power
                        double limitScale = distanceFromMaxLimit / WARNING_ZONE;
                        power *= limitScale;
                        telemetry.addData("WARNING", "Approaching RIGHT limit");
                    }
                }

                // velocity safety
                if (Math.abs(velocity) > 1200) {
                    power *= 0.5;
                }

                power = Math.max(-0.4, Math.min(0.4, power));

                turret.setPower(-power);
            } else {
                turret.setPower(0);
            }

            telemetry.addData("tx", result != null ? result.getTx() : "null");
            telemetry.addData("vel", turret.getVelocity());
            telemetry.addData("Turret Angle", "%.1f°", currentAngle);
            telemetry.addData("Angle Range", "%.1f° to %.1f°", MIN_ANGLE, MAX_ANGLE);
            telemetry.addData("Encoder Ticks", "%.0f", currentTicks);
            telemetry.update();
        }
    }
}
