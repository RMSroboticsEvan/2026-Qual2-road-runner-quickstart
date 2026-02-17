package org.firstinspires.ftc.teamcode.Common;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Flywheel;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Intake;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Spindexer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Transfer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretHardware;

/**
 * Base class for Autonomous programs that eliminates Blue/Red code duplication.
 * Handles coordinate mirroring for Red alliance automatically.
 *
 * NEW: Optional turret auto-align during autonomous to recover from being bumped
 */
public abstract class BaseAutonomousTopV2 extends LinearOpMode {
    protected Spindexer spindexer;
    protected Intake intake;
    protected Transfer transfer;
    protected Flywheel flywheel;
    protected TurretHardware turret;
    protected AllianceConfig config;
    protected MecanumDrive drive;
    protected TurretController turretController;

    // Configuration: Enable auto-align during autonomous?
    // Set to true to use HYBRID mode (recovers from bumps), false for hardcoded angles
    protected boolean useAutoAlign = false;

    // Flywheel PIDF coefficients
    private static final double PIDF_P = 150;
    private static final double PIDF_I = 0;
    private static final double PIDF_D = 0;
    private static final double PIDF_F = 11.7025;

    // Timing constants
    private static final int FLYWHEEL_SPINUP_MS = 500;
    private static final int SHOOTING_DURATION_MS = 2750;
    private static final int SHOOTING_DURATION_SHORT_MS = 2250;

    // Flywheel speed
    private static final double FLYWHEEL_SPEED = 0.8;
    private static final double FLYWHEEL_MAX_VELOCITY = 1600;

    // Spindexer speeds
    private static final double SPINDEXER_IDLE = 0.4;

    /**
     * Subclasses must implement this to provide alliance-specific configuration
     */
    protected abstract AllianceConfig getAllianceConfig();

    @Override
    public void runOpMode() throws InterruptedException {
        config = getAllianceConfig();
        initializeHardware();
        buildActions();

        waitForStart();
        if (isStopRequested()) return;

        executeAutonomous();
    }

    /**
     * Initialize all hardware components
     */
    private void initializeHardware() {
        turret = new TurretHardware(hardwareMap);
        flywheel = new Flywheel(hardwareMap);
        intake = new Intake(hardwareMap);
        spindexer = new Spindexer(hardwareMap);
        transfer = new Transfer(hardwareMap);

        flywheel.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidf = new PIDFCoefficients(PIDF_P, PIDF_I, PIDF_D, PIDF_F);
        flywheel.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        // Initialize drive with alliance-specific starting position
        Pose2d startPose = new Pose2d(
            config.getStartX(),
            config.getStartY(),
            Math.toRadians(config.getStartHeading())
        );
        drive = new MecanumDrive(hardwareMap, startPose);

        // Initialize turret controller
        // Use HYBRID mode if auto-align enabled, otherwise MANUAL for hardcoded angles
        TurretController.AimingMode mode = useAutoAlign ?
            TurretController.AimingMode.HYBRID : TurretController.AimingMode.MANUAL;
        turretController = new TurretController(turret, drive, mode);
    }

    // Actions (will be built with alliance-specific coordinates)
    protected Action shootPreload;
    protected Action pickUpBallsOneP1, pickUpBallsOneP2;
    protected Action pickUpBallsTwoP1, pickUpBallsTwoP2;
    protected Action pickUpBallsThreeP1, pickUpBallsThreeP2;
    protected Action pickUpBallsFourP1, pickUpBallsFourP2, pickUpBallsFourP3;
    protected Action leaveShootingZone;

    /**
     * Build all Road Runner actions with alliance-specific coordinates
     */
    private void buildActions() {
        double y = config.mirrorY(1); // Multiplier for Y coordinates

        // Starting position
        Pose2d start = new Pose2d(-48, -48 * y, Math.toRadians(config.isBlue() ? 225 : 135));

        // Shoot Preload
        shootPreload = drive.actionBuilder(start)
                .strafeToLinearHeading(new Vector2d(-10, -6 * y), Math.toRadians(config.isBlue() ? 315 : 45))
                .build();

        // Second Row
        Pose2d afterPreload = new Pose2d(-10, -6 * y, Math.toRadians(config.isBlue() ? 315 : 45));
        pickUpBallsOneP1 = drive.actionBuilder(afterPreload)
                .splineToLinearHeading(
                    new Pose2d(16, -67 * y, Math.toRadians(config.isBlue() ? 270 : 90)),
                    Math.toRadians(config.isBlue() ? 275 : 85)
                )
                .build();
        pickUpBallsOneP2 = drive.actionBuilder(new Pose2d(16, -67 * y, Math.toRadians(config.isBlue() ? 270 : 90)))
                .strafeToLinearHeading(new Vector2d(-14, -6 * y), Math.toRadians(config.isBlue() ? 315 : 45))
                .build();

        // Gate Area
        Pose2d afterBallsOne = new Pose2d(-14, -6 * y, Math.toRadians(config.isBlue() ? 315 : 45));
        pickUpBallsTwoP1 = drive.actionBuilder(afterBallsOne)
                .splineToLinearHeading(
                    new Pose2d(10, -62 * y, Math.toRadians(config.isBlue() ? 235 : 305)),
                    Math.toRadians(config.isBlue() ? 250 : 290)
                )
                .build();
        pickUpBallsTwoP2 = drive.actionBuilder(new Pose2d(10, -62 * y, Math.toRadians(config.isBlue() ? 235 : 305)))
                .strafeToLinearHeading(new Vector2d(-14, -6 * y), Math.toRadians(config.isBlue() ? 270 : 90))
                .build();

        // First Row
        Pose2d afterBallsTwo = new Pose2d(-14, -6 * y, Math.toRadians(config.isBlue() ? 270 : 90));
        pickUpBallsThreeP1 = drive.actionBuilder(afterBallsTwo)
                .strafeToLinearHeading(new Vector2d(-10, -55 * y), Math.toRadians(config.isBlue() ? 270 : 90))
                .build();
        pickUpBallsThreeP2 = drive.actionBuilder(new Pose2d(-10, -55 * y, Math.toRadians(config.isBlue() ? 270 : 90)))
                .strafeToLinearHeading(new Vector2d(-14, -6 * y), Math.toRadians(config.isBlue() ? 315 : 45))
                .build();

        // Third Row
        Pose2d afterBallsThree = new Pose2d(-14, -6 * y, Math.toRadians(config.isBlue() ? 315 : 45));
        pickUpBallsFourP1 = drive.actionBuilder(afterBallsThree)
                .strafeToLinearHeading(new Vector2d(40, -35 * y), Math.toRadians(config.isBlue() ? 270 : 90))
                .build();
        pickUpBallsFourP2 = drive.actionBuilder(new Pose2d(40, -35 * y, Math.toRadians(config.isBlue() ? 270 : 90)))
                .strafeToLinearHeading(new Vector2d(40, -67 * y), Math.toRadians(config.isBlue() ? 270 : 90))
                .build();
        pickUpBallsFourP3 = drive.actionBuilder(new Pose2d(40, -67 * y, Math.toRadians(config.isBlue() ? 270 : 90)))
                .strafeToLinearHeading(new Vector2d(-14, -6 * y), Math.toRadians(config.isBlue() ? 315 : 45))
                .build();

        // Leave Shooting Zone
        Pose2d finalShootPos = new Pose2d(-14, -6 * y, Math.toRadians(config.isBlue() ? 315 : 45));
        leaveShootingZone = drive.actionBuilder(finalShootPos)
                .strafeToLinearHeading(new Vector2d(4, -55 * y), Math.toRadians(0))
                .build();
    }

    /**
     * Execute the full autonomous sequence
     */
    private void executeAutonomous() throws InterruptedException {
        // Start and shoot preload
        flywheel.flywheel.setVelocity(FLYWHEEL_SPEED * FLYWHEEL_MAX_VELOCITY);
        sleep(FLYWHEEL_SPINUP_MS);
        intake.runIntake(1);
        aimTurret(config.isBlue() ? -86 : 86); // Preload shooting position
        Actions.runBlocking(shootPreload);
        shootBall(SHOOTING_DURATION_MS);

        // Pick up and shoot second row balls
        Actions.runBlocking(new ParallelAction(
                new SequentialAction(pickUpBallsOneP1, pickUpBallsOneP2)
        ));
        shootBall(SHOOTING_DURATION_SHORT_MS);

        // NOTE: Temporary stop removed from original code - continuing with full sequence

        // Pick up and shoot gate area balls
        Actions.runBlocking(new ParallelAction(
                new SequentialAction(pickUpBallsTwoP1, pickUpBallsTwoP2)
        ));
        shootBall(SHOOTING_DURATION_SHORT_MS);
        aimTurret(config.isBlue() ? -45 : 45); // Adjust angle for new position

        // Pick up and shoot first row balls
        Actions.runBlocking(new ParallelAction(
                new SequentialAction(pickUpBallsThreeP1, pickUpBallsThreeP2)
        ));
        shootBall(SHOOTING_DURATION_SHORT_MS);
        aimTurret(config.isBlue() ? -90 : 90); // Adjust angle for new position

        // Pick up and shoot third row balls
        Actions.runBlocking(new ParallelAction(
                new SequentialAction(pickUpBallsFourP1, pickUpBallsFourP2, pickUpBallsFourP3)
        ));
        shootBall(SHOOTING_DURATION_SHORT_MS);

        // Stop systems and leave shooting zone
        spindexer.spindexer.setPower(0);
        intake.intake.setPower(0);
        Actions.runBlocking(leaveShootingZone);

        sleep(50000); // Final stop to prevent re-looping
    }

    /**
     * Aim turret using either hardcoded angle (default) or auto-align (if enabled)
     * @param fallbackAngle The hardcoded angle to use if auto-align is disabled
     */
    private void aimTurret(double fallbackAngle) throws InterruptedException {
        if (useAutoAlign) {
            // Update drive odometry
            drive.updatePoseEstimate();

            // Use turret controller in HYBRID mode (auto-aligns with AprilTag correction)
            turretController.update();

            // Give it a moment to align
            sleep(200);
        } else {
            // Use hardcoded angle
            turretController.setManualAngle(fallbackAngle);
        }
    }

    /**
     * Execute ball shooting sequence
     */
    private void shootBall(int durationMs) throws InterruptedException {
        spindexer.spindexer.setPower(config.getShootingSpindexerPower());
        transfer.transferUp(1);
        sleep(durationMs);
        transfer.transferDown(1);
        spindexer.spindexer.setPower(SPINDEXER_IDLE);
    }
}
