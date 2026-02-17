package org.firstinspires.ftc.teamcode.Common;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.DriveTrain;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Flywheel;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Intake;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Spindexer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Transfer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretHardware;

/**
 * Base class for TeleOp programs that eliminates Blue/Red code duplication.
 * Subclasses only need to provide AllianceConfig.
 */
public abstract class BaseTeleOp extends LinearOpMode {
    protected DriveTrain driveTrain;
    protected Spindexer spindexer;
    protected Intake intake;
    protected Transfer transfer;
    protected Flywheel flywheel;
    protected TurretHardware turret;
    protected MecanumDrive drive;
    protected TurretController turretController;
    protected AllianceConfig config;

    protected double speed;

    // PIDF coefficients for flywheel (shared across alliances)
    private static final double PIDF_P = 150;
    private static final double PIDF_I = 0;
    private static final double PIDF_D = 0;
    private static final double PIDF_F = 11.7025;

    // Speed adjustment constants
    private static final double SPEED_INCREMENT = 0.025;
    private static final double FLYWHEEL_MAX_VELOCITY = 1600;
    private static final double FLYWHEEL_IDLE_POWER = 0.8;

    // Spindexer constants
    private static final double SPINDEXER_REVERSE_POWER = -0.2;
    private static final int SHOOTING_DELAY_MS = 200;

    /**
     * Subclasses must implement this to provide alliance-specific configuration
     */
    protected abstract AllianceConfig getAllianceConfig();

    @Override
    public void runOpMode() throws InterruptedException {
        // Get alliance-specific configuration
        config = getAllianceConfig();

        // Initialize hardware
        initializeHardware();

        // Set initial speed from config
        speed = config.getInitialFlywheelSpeed();

        waitForStart();

        while (!isStopRequested() && opModeIsActive()) {
            updateTelemetry();
            handleDrivetrain();
            handleTurret();
            handleFlywheel();
            handleIntakeSystem();

            telemetry.update();
        }
    }

    /**
     * Initialize all hardware components
     */
    private void initializeHardware() {
        driveTrain = new DriveTrain(hardwareMap);
        spindexer = new Spindexer(hardwareMap);
        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        flywheel = new Flywheel(hardwareMap);
        turret = new TurretHardware(hardwareMap);

        // Initialize Road Runner for odometry-based turret aiming
        drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, Math.toRadians(180)));

        // Initialize turret controller (starts in HYBRID mode)
        turretController = new TurretController(turret, drive, TurretController.AimingMode.HYBRID);

        // Configure flywheel
        flywheel.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidf = new PIDFCoefficients(PIDF_P, PIDF_I, PIDF_D, PIDF_F);
        flywheel.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
    }

    /**
     * Update telemetry with current status
     */
    private void updateTelemetry() {
        telemetry.addData("Alliance", config.getAlliance());
        telemetry.addData("current", spindexer.spindexer.getCurrentPosition());
        telemetry.addData("target", spindexer.spindexer.getTargetPosition());
        telemetry.addData("vel", flywheel.returnVel());
        telemetry.addData("balls", spindexer.ballCount);
        telemetry.addData("speed", speed);
        telemetry.addData("Turret", turretController.getTelemetryString());
    }

    /**
     * Handle drivetrain controls
     */
    private void handleDrivetrain() {
        driveTrain.driveMotors(
            gamepad1.left_stick_y,
            gamepad1.right_stick_x,
            gamepad1.left_stick_x
        );

        // Update odometry for turret aiming
        drive.updatePoseEstimate();
    }

    /**
     * Handle turret aiming system
     *
     * Controls:
     * - Triangle (gamepad1): Cycle through aiming modes (MANUAL → ODOMETRY → HYBRID → APRILTAG)
     * - Cross (gamepad1): Reset odometry with AprilTag (use at reset corner)
     * - D-pad left/right (gamepad2): Manual turret control (only in MANUAL mode)
     */
    private void handleTurret() {
        // Cycle aiming modes
        if (gamepad1.triangleWasPressed()) {
            turretController.cycleAimingMode();
        }

        // Reset odometry at reset corner
        if (gamepad1.crossWasPressed()) {
            turretController.resetOdometryWithAprilTag();
            // Also reset drive pose to known position
            drive.localizer.setPose(new Pose2d(59, -59, Math.toRadians(0)));
        }

        // Manual turret control (only works in MANUAL mode)
        if (turretController.getAimingMode() == TurretController.AimingMode.MANUAL) {
            if (gamepad2.dpad_left) {
                // Turn left manually (increment by small amount each frame)
                double currentAngle = turret.turret.getPosition() * turret.conversion - 0.505;
                turretController.setManualAngle(currentAngle - 2);
            } else if (gamepad2.dpad_right) {
                // Turn right manually
                double currentAngle = turret.turret.getPosition() * turret.conversion - 0.505;
                turretController.setManualAngle(currentAngle + 2);
            }
        }

        // Update turret position based on current mode
        turretController.update();
    }

    /**
     * Handle flywheel speed adjustments and operation
     */
    private void handleFlywheel() {
        // Speed adjustment
        if (gamepad2.squareWasPressed()) {
            speed = speed - SPEED_INCREMENT;
        } else if (gamepad2.circleWasPressed()) {
            speed = speed + SPEED_INCREMENT;
        }

        // Flywheel operation
        if (isRobotStationary()) {
            flywheel.flywheel.setVelocity(speed * FLYWHEEL_MAX_VELOCITY);
            driveTrain.stopMotor();
        } else {
            flywheel.flywheel.setPower(FLYWHEEL_IDLE_POWER);
        }
    }

    /**
     * Handle intake, spindexer, and transfer system
     */
    private void handleIntakeSystem() {
        if (gamepad2.right_bumper) {
            // Shooting mode
            spindexer.spindexer.setPower(config.getShootingSpindexerPower());
            sleep(SHOOTING_DELAY_MS);
            transfer.transferUp(1);
            intake.runIntake(1);
        } else if (gamepad2.left_bumper) {
            // Reverse mode
            intake.runIntake(-1);
            spindexer.spindexer.setPower(SPINDEXER_REVERSE_POWER);
        } else {
            // Normal intake mode
            spindexer.spindexer.setPower(config.getNormalSpindexerPower());
            intake.runIntake(1);
            transfer.transferDown(1);
        }
    }

    /**
     * Check if robot is stationary (no joystick input)
     */
    private boolean isRobotStationary() {
        return gamepad1.left_stick_y == 0
            && gamepad1.right_stick_x == 0
            && gamepad1.left_stick_x == 0;
    }
}
