package org.firstinspires.ftc.teamcode.ProgrammingBoards;

import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * ⚠️ ACTION REQUIRED: Review and decide whether to delete this file
 *
 * STATUS: UNTRACKED - Never committed by anyone (created Jan 31, 2025)
 *
 * CURRENT REALITY: Robot uses TurretHardware.java (standard Servo), NOT this file
 * - TurretHardware.java: Uses standard Servo "axon" (positional, 0-1 range)
 * - This file: Alternative CRServo approach (continuous rotation + external encoder)
 *
 * WHY THIS EXISTS: Exploratory work from Jan 31 for alternative turret implementation
 *
 * CAN BE DELETED IF:
 * - Current TurretV2.java servo approach is working well
 * - Team is satisfied with standard servo performance
 * - No plans to switch to CRServo approach
 *
 * KEEP IF:
 * - Team wants CRServo option for better range/control
 * - Current servo has limitations (rotation range, accuracy, etc.)
 * - Want to preserve this alternative implementation
 *
 * ---
 *
 * TurretV2 with CRServo + External Encoder
 *
 * This version uses a CRServo instead of motor to save motor count (FTC 8-motor limit).
 * Requires external encoder for position feedback.
 *
 * Hardware Setup:
 * - CRServo: Connected to "turret" in robot config
 * - External Encoder: Connected to spare motor port (e.g., "rightBack" or dedicated "turretEncoder")
 *   Options:
 *   1. REV Through Bore Encoder (8192 CPR) mounted on turret shaft
 *   2. Spare motor port from drivetrain used as encoder input
 *   3. Dead wheel encoder
 */
public class TurretV2Servo
{
    public Limelight3A limelight;

    // Servo for actuation (saves a motor!)
    public CRServo turretServo;

    // External encoder for position tracking
    public OverflowEncoder turretEncoder;

    double kP = 0.03, kI=0.00025;
    double integral = 0;
    double prevTargetAngle = 0;

    // Encoder configuration constants
    // IMPORTANT: Update these based on your actual encoder setup!

    // OPTION 1: REV Through Bore Encoder (8192 CPR) on turret shaft after pulley
    // private final double ENCODER_CPR = 8192.0;
    // private final double TICKS_PER_DEGREE = 8192.0 / 360.0;  // = 22.76 ticks/degree

    // OPTION 2: Motor encoder (GoBILDA 435 RPM = 495.6 ticks/rev) on motor shaft before pulley
    // Pulley ratio: 110/48 = 2.2917
    private final double ENCODER_CPR = 495.6;  // 28 CPR × 17.7 gear ratio
    private final double PULLEY_RATIO = 110.0 / 48.0;  // 2.2917
    private final double TICKS_PER_DEGREE = (ENCODER_CPR * PULLEY_RATIO) / 360.0;  // = 3.155 ticks/degree

    public TurretV2Servo(HardwareMap hardwareMap){
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");

        // pipeline 0 is a placeholder, put apriltag pipeline when configured
        limelight.pipelineSwitch(0);

        // Initialize CRServo for turret actuation
        turretServo = hardwareMap.get(CRServo.class, "turret");

        // Initialize external encoder for position feedback
        // OPTION A: Using REV Through Bore Encoder or dedicated encoder port
        // Uncomment this if you have a dedicated encoder port:
        // turretEncoder = new OverflowEncoder(new RawEncoder(hardwareMap.get(DcMotorEx.class, "turretEncoder")));

        // OPTION B: Using spare motor port (e.g., if you have an unused drivetrain encoder)
        // Change "rightBack" to whatever unused motor port you're using for encoder input
        turretEncoder = new OverflowEncoder(new RawEncoder(hardwareMap.get(DcMotorEx.class, "rightBack")));

        limelight.start();
    }

    /**
     * Get current turret position in encoder ticks
     * This replaces turret.getCurrentPosition() from motor version
     */
    public double getCurrentPosition() {
        return turretEncoder.getPositionAndVelocity().position;
    }

    /**
     * Get current turret velocity in ticks/sec
     * This replaces turret.getVelocity() from motor version
     */
    public double getVelocity() {
        return turretEncoder.getPositionAndVelocity().velocity;
    }

    /**
     * Set turret servo power (-1.0 to 1.0)
     * This replaces turret.setPower() from motor version
     */
    public void setPower(double power) {
        turretServo.setPower(power);
    }

    /**
     * Reset encoder position to zero
     * Call this when turret is at center position
     */
    public void resetEncoder() {
        // Note: OverflowEncoder doesn't have a direct reset method
        // You'll need to track the offset manually or use the starting position
        // Alternatively, use an absolute encoder like REV Through Bore
    }

    private double toTicks(double angle)
    {
        return angle * TICKS_PER_DEGREE;
    }

    private double toDegrees(double ticks)
    {
        return ticks / TICKS_PER_DEGREE;
    }
}
