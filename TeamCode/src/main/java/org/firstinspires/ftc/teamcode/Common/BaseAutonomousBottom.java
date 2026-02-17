package org.firstinspires.ftc.teamcode.Common;

import com.acmerobotics.roadrunner.Pose2d;
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
 * Base class for Autonomous programs starting from BOTTOM position.
 * Handles coordinate mirroring for Red alliance automatically.
 *
 * This is a placeholder for Bottom autonomous routines.
 * Starting position is different from Top (see getAllianceConfig() implementation).
 *
 * TODO: Implement Bottom autonomous strategy
 * TODO: Define ball pickup sequence for Bottom position
 * TODO: Set correct starting coordinates for Bottom position
 */
public abstract class BaseAutonomousBottom extends LinearOpMode {
    protected Spindexer spindexer;
    protected Intake intake;
    protected Transfer transfer;
    protected Flywheel flywheel;
    protected TurretHardware turret;
    protected AllianceConfig config;
    protected MecanumDrive drive;
    protected TurretController turretController;

    // Configuration: Enable auto-align during autonomous?
    protected boolean useAutoAlign = false;

    // Flywheel PIDF coefficients
    private static final double PIDF_P = 150;
    private static final double PIDF_I = 0;
    private static final double PIDF_D = 0;
    private static final double PIDF_F = 11.7025;

    /**
     * Subclasses must implement this to provide alliance-specific configuration.
     * Starting position should be set for BOTTOM position (not Top).
     */
    protected abstract AllianceConfig getAllianceConfig();

    @Override
    public void runOpMode() throws InterruptedException {
        config = getAllianceConfig();
        initializeHardware();

        telemetry.addData("Status", "Initialized - BOTTOM Position");
        telemetry.addData("Alliance", config.getAlliance());
        telemetry.addData("TODO", "Bottom autonomous not yet implemented");
        telemetry.update();

        waitForStart();
        if (isStopRequested()) return;

        // TODO: Implement autonomous routine for Bottom position
        telemetry.addData("Status", "Running - Bottom autonomous not implemented");
        telemetry.update();

        // Placeholder - prevent re-looping
        sleep(30000);
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

        // TODO: Set correct starting position for BOTTOM
        // This is a placeholder - adjust coordinates for Bottom position
        Pose2d startPose = new Pose2d(
            config.getStartX(),
            config.getStartY(),
            Math.toRadians(config.getStartHeading())
        );
        drive = new MecanumDrive(hardwareMap, startPose);

        // Initialize turret controller
        TurretController.AimingMode mode = useAutoAlign ?
            TurretController.AimingMode.HYBRID : TurretController.AimingMode.MANUAL;
        turretController = new TurretController(turret, drive, mode);
    }
}
