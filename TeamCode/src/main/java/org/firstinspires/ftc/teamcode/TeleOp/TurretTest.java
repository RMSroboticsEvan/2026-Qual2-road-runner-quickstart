package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretV2;


@TeleOp
public class TurretTest extends LinearOpMode {
    private TurretV2 turret;
    private Limelight3A limelight;
    @Override
    public void runOpMode() throws InterruptedException {
        turret = new TurretV2(hardwareMap);
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");

        // pipeline 0 is a placeholder, put apriltag pipeline when configured
        limelight.pipelineSwitch(0);

        limelight.start();


        waitForStart();

        // Reset encoder to 0 at start (assume starting at center position)
        turret.turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        double kP = 0.015;
        double kD = 0.003;

        // ANGLE LIMITS (adjust these based on your physical constraints)
        final double MIN_ANGLE = -90.0;  // degrees (left limit)
        final double MAX_ANGLE = 90.0;   // degrees (right limit)
        final double WARNING_ZONE = 10.0; // degrees from limit to start slowing
        // GoBILDA 435 RPM motor (28 CPR × 17.7 = 495.6) with 48:110 pulley = 3.155 ticks/degree
        final double TICKS_PER_DEGREE = 3.15493055556;

        while (opModeIsActive()&& !isStopRequested()) {
            LLResult result = limelight.getLatestResult();

            // Get current turret angle
            double currentTicks = turret.turret.getCurrentPosition();
            double currentAngle = currentTicks / TICKS_PER_DEGREE;

            if (result != null && result.isValid()) {
                double error = result.getTx();

                // deadband
                if (Math.abs(error) < 1.5) {
                    turret.turret.setPower(0);
                    continue;
                }

                double velocity = turret.turret.getVelocity();

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

                turret.turret.setPower(-power);
            } else {
                turret.turret.setPower(0);
            }

            telemetry.addData("tx", result != null ? result.getTx() : "null");
            telemetry.addData("vel", turret.turret.getVelocity());
            telemetry.addData("Turret Angle", "%.1f°", currentAngle);
            telemetry.addData("Angle Range", "%.1f° to %.1f°", MIN_ANGLE, MAX_ANGLE);
            telemetry.update();
        }


    }
}
