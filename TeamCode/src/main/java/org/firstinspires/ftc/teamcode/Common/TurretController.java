package org.firstinspires.ftc.teamcode.Common;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.hardware.limelightvision.LLResult;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretHardware;

/**
 * Unified turret control system with multiple aiming modes.
 * Can be used in both TeleOp and Autonomous.
 *
 * Aiming Modes:
 * - MANUAL: Driver controls turret directly
 * - ODOMETRY: Auto-aim using drivetrain odometry (fast, but drifts over time)
 * - APRILTAG: Auto-aim using Limelight AprilTag detection (accurate, requires line of sight)
 * - HYBRID: Uses odometry, corrects with AprilTag when available
 */
public class TurretController {

    public enum AimingMode {
        MANUAL,      // Driver control only
        ODOMETRY,    // Use drivetrain position to calculate angle
        APRILTAG,    // Use Limelight AprilTag detection
        HYBRID       // Odometry + AprilTag correction when available
    }

    private final TurretHardware turret;
    private final MecanumDrive drive;
    private AimingMode currentMode;

    // Target position on field (where we're shooting at)
    private static final double TARGET_X = -63;  // inches
    private static final double TARGET_Y = 58;   // inches

    // AprilTag deadband (don't adjust if within this range)
    private static final double APRILTAG_DEADBAND = 1.0; // degrees

    // Odometry drift threshold (switch to AprilTag if error exceeds this)
    private static final double ODOMETRY_ERROR_THRESHOLD = 5.0; // degrees

    private double lastOdometryAngle = 0;
    private double aprilTagCorrectionOffset = 0;

    /**
     * Create a TurretController
     * @param turret The turret hardware
     * @param drive The mecanum drive for odometry (can be null if only using manual/AprilTag)
     * @param initialMode Starting aiming mode
     */
    public TurretController(TurretHardware turret, MecanumDrive drive, AimingMode initialMode) {
        this.turret = turret;
        this.drive = drive;
        this.currentMode = initialMode;
    }

    /**
     * Update turret position based on current aiming mode
     * Call this every loop iteration
     */
    public void update() {
        switch (currentMode) {
            case MANUAL:
                // Do nothing - driver controls via setManualAngle()
                break;

            case ODOMETRY:
                updateOdometryAiming();
                break;

            case APRILTAG:
                updateAprilTagAiming();
                break;

            case HYBRID:
                updateHybridAiming();
                break;
        }
    }

    /**
     * Set turret angle manually (for MANUAL mode or autonomous hardcoded angles)
     */
    public void setManualAngle(double degrees) {
        turret.turnTo(degrees);
    }

    /**
     * Switch to a different aiming mode
     */
    public void setAimingMode(AimingMode mode) {
        this.currentMode = mode;

        // Reset offsets when switching modes
        if (mode == AimingMode.ODOMETRY || mode == AimingMode.HYBRID) {
            aprilTagCorrectionOffset = 0;
        }
    }

    /**
     * Get current aiming mode
     */
    public AimingMode getAimingMode() {
        return currentMode;
    }

    /**
     * Cycle to next aiming mode (for driver toggle button)
     * Order: MANUAL → ODOMETRY → HYBRID → APRILTAG → MANUAL
     */
    public void cycleAimingMode() {
        switch (currentMode) {
            case MANUAL:
                setAimingMode(AimingMode.ODOMETRY);
                break;
            case ODOMETRY:
                setAimingMode(AimingMode.HYBRID);
                break;
            case HYBRID:
                setAimingMode(AimingMode.APRILTAG);
                break;
            case APRILTAG:
                setAimingMode(AimingMode.MANUAL);
                break;
        }
    }

    /**
     * Reset odometry offset using AprilTag (like going to reset corner)
     * Call this when driver confirms robot is at known position
     */
    public void resetOdometryWithAprilTag() {
        if (drive == null) return;

        LLResult result = turret.limelight.getLatestResult();
        if (result != null && result.isValid()) {
            // Use AprilTag as ground truth to reset odometry offset
            aprilTagCorrectionOffset = 0;

            // Could also reset drive pose here if you know the AprilTag location
            // This would require AprilTag ID and known field positions
        }
    }

    /**
     * Get current turret angle error from AprilTag (for telemetry)
     */
    public double getAprilTagError() {
        double tx = turret.returnTx();
        return (tx == 1000) ? Double.NaN : tx; // 1000 = no target
    }

    /**
     * Check if AprilTag is visible
     */
    public boolean isAprilTagVisible() {
        double tx = turret.returnTx();
        return tx != 1000; // TurretV2 returns 1000 when no target
    }

    // ========== Private Helper Methods ==========

    /**
     * Update turret using odometry-based aiming
     */
    private void updateOdometryAiming() {
        if (drive == null) return;

        Pose2d currentPose = drive.localizer.getPose();

        // Calculate angle to target using atan2
        double dx = TARGET_X - currentPose.position.x;
        double dy = TARGET_Y - currentPose.position.y;
        double angleToTarget = Math.toDegrees(Math.atan2(dy, dx));

        // Adjust for robot heading
        double robotHeading = Math.toDegrees(currentPose.heading.toDouble());
        double targetAngle = wrapAngle(-robotHeading + angleToTarget);

        lastOdometryAngle = targetAngle;
        turret.turnTo(targetAngle);
    }

    /**
     * Update turret using AprilTag-based aiming
     */
    private void updateAprilTagAiming() {
        double tx = turret.returnTx();

        // No target visible
        if (tx == 1000) return;

        // Within deadband, don't move
        if (Math.abs(tx) < APRILTAG_DEADBAND) return;

        // Use TurretV2's built-in AprilTag aiming
        turret.TurnToAT();
    }

    /**
     * Update turret using hybrid odometry + AprilTag aiming
     * Uses odometry normally, but corrects with AprilTag when available
     */
    private void updateHybridAiming() {
        if (drive == null) {
            // Fall back to AprilTag only
            updateAprilTagAiming();
            return;
        }

        // Calculate odometry-based angle
        Pose2d currentPose = drive.localizer.getPose();
        double dx = TARGET_X - currentPose.position.x;
        double dy = TARGET_Y - currentPose.position.y;
        double angleToTarget = Math.toDegrees(Math.atan2(dy, dx));
        double robotHeading = Math.toDegrees(currentPose.heading.toDouble());
        double odometryAngle = wrapAngle(-robotHeading + angleToTarget);

        // Check if AprilTag is visible
        double tx = turret.returnTx();
        boolean aprilTagVisible = (tx != 1000);

        if (aprilTagVisible && Math.abs(tx) > APRILTAG_DEADBAND) {
            // AprilTag sees error - update correction offset
            aprilTagCorrectionOffset = tx;

            // If odometry error is large, prefer AprilTag
            if (Math.abs(tx) > ODOMETRY_ERROR_THRESHOLD) {
                turret.TurnToAT();
                return;
            }
        }

        // Use odometry with AprilTag correction
        double correctedAngle = odometryAngle - aprilTagCorrectionOffset;
        turret.turnTo(correctedAngle);
        lastOdometryAngle = correctedAngle;
    }

    /**
     * Wrap angle to -180 to +180 range
     */
    private double wrapAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    /**
     * Get telemetry data for debugging
     */
    public String getTelemetryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mode: ").append(currentMode);
        sb.append(" | AT Visible: ").append(isAprilTagVisible());

        if (currentMode == AimingMode.ODOMETRY || currentMode == AimingMode.HYBRID) {
            sb.append(" | Odo Angle: ").append(String.format("%.1f°", lastOdometryAngle));
        }

        if (currentMode == AimingMode.APRILTAG || currentMode == AimingMode.HYBRID) {
            double error = getAprilTagError();
            if (!Double.isNaN(error)) {
                sb.append(" | AT Error: ").append(String.format("%.1f°", error));
            }
        }

        if (currentMode == AimingMode.HYBRID) {
            sb.append(" | Correction: ").append(String.format("%.1f°", aprilTagCorrectionOffset));
        }

        return sb.toString();
    }
}
