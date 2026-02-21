package org.firstinspires.ftc.teamcode.Autons;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.DriveTrain;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Flywheel;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Intake;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Spindexer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Transfer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretV2;

@Autonomous
public class AutoRedFar extends LinearOpMode {
    private DriveTrain driveTrain;
    private Spindexer spindexer;
    private Intake intake;
    private Transfer transfer;
    private Flywheel flywheel;
    private TurretV2 turret;

    @Override
    public void runOpMode() throws InterruptedException {
        turret = new TurretV2(hardwareMap);
        flywheel = new Flywheel (hardwareMap);
        intake = new Intake (hardwareMap);
        spindexer = new Spindexer (hardwareMap);
        transfer = new Transfer (hardwareMap);
        flywheel.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidf = new PIDFCoefficients(150, 0, 0, 11.7025);
        flywheel.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(62,16, Math.toRadians(180)));

        //Third Row
        Action pickUpBallsOneP1 = drive.actionBuilder(new Pose2d(62, 16, Math.toRadians(180)))
                .splineToSplineHeading(new Pose2d(29, 25, Math.toRadians(90)), Math.toRadians(270))
                .build();
        Action pickUpBallsOneP2 = drive.actionBuilder(new Pose2d(29, 25, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(29, 58), Math.toRadians(90))
                .build();
        Action pickUpBallsOneP3 = drive.actionBuilder(new Pose2d(29, 58, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(62, 16), Math.toRadians(180))
                .build();

        //Human Player
        Action pickUpBallsTwoP1 = drive.actionBuilder(new Pose2d(62, 16, Math.toRadians(180)))
                .strafeToLinearHeading(new Vector2d(56, 16), Math.toRadians(90))
                .build();
        Action pickUpBallsTwoP2 = drive.actionBuilder(new Pose2d(56, 16, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(75, 65), Math.toRadians(90))
                .build();
        Action pickUpBallsTwoP3 = drive.actionBuilder(new Pose2d(75, 65, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(75, 35), Math.toRadians(90))
                .build();
        Action pickUpBallsTwoP4 = drive.actionBuilder(new Pose2d(75, 35, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(75, 65), Math.toRadians(90))
                .build();
        Action pickUpBallsTwoP5 = drive.actionBuilder(new Pose2d(75, 65, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(56, 16), Math.toRadians(90))
                .build();
        Action pickUpBallsTwoP6 = drive.actionBuilder(new Pose2d(56, 16, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(62, 16), Math.toRadians(180))
                .build();

        //Leave Shooting Zone
        Action leaveShootingZone = drive.actionBuilder(new Pose2d(62,16,Math.toRadians(180)))
                .strafeToLinearHeading(new Vector2d(35, 16), Math.toRadians(180))
                .build();


        waitForStart();
        if (isStopRequested()) return;

        while(!isStopRequested() && opModeIsActive()) {
            //Start and shoot preload
            flywheel.flywheel.setVelocity(0.95 * 1600);
            intake.runIntake(1);
            turret.turnTo(-21);
            sleep(2000);
            transfer.transferUp(1);
            spindexer.spindexer.setPower(0.125);
            sleep(3500);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.2);

            //Pick up balls from third row
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsOneP1,
                                    pickUpBallsOneP2
                            )
                    )
            );
            sleep(750);
            Actions.runBlocking(pickUpBallsOneP3);

            //Shoot balls that came from third row
            flywheel.flywheel.setVelocity(0.975 * 1600);
            turret.turnTo(-17);
            sleep(250);
            spindexer.spindexer.setPower(0.125);
            transfer.transferUp(1);
            sleep(3500);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.2);

            //Pick up balls from human player
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsTwoP1,
                                    pickUpBallsTwoP2
                            )
                    )
            );
            sleep(750);
            flywheel.flywheel.setVelocity(0.975 * 1600);
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsTwoP3,
                                    pickUpBallsTwoP4
                            )
                    )
            );
            sleep(750);
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsTwoP5,
                                    pickUpBallsTwoP6
                            )
                    )
            );

            //Shoot balls that came from human player
            turret.turnTo(-17.5);
            sleep(500);
            spindexer.spindexer.setPower(0.125);
            transfer.transferUp(1);
            sleep(3500);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.2);

            //Leave shooting zone
            Actions.runBlocking(leaveShootingZone);

            //Turn everything off
            turret.turnTo(0);
            spindexer.spindexer.setPower(0);
            intake.intake.setPower(0);
            flywheel.flywheel.setVelocity(0 * 1600);

            sleep(50000);
        }
    }}
