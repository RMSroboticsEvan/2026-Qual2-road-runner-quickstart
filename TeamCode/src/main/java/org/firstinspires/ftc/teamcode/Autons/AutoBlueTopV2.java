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
public class AutoBlueTopV2 extends LinearOpMode {
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

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(-48,-48, Math.toRadians(225)));

        //Shoot Preload
        Action shootPreload = drive.actionBuilder(new Pose2d(-48,-48,Math.toRadians(225)))
                .strafeToLinearHeading(new Vector2d(-10, -6), Math.toRadians(315))
                .build();

        //Second Row
        Action pickUpBallsOneP1 = drive.actionBuilder(new Pose2d(-10, -6, Math.toRadians(315)))
                .splineToLinearHeading(new Pose2d(16, -67, Math.toRadians(270)), Math.toRadians(275))
                .build();
        Action pickUpBallsOneP2 = drive.actionBuilder(new Pose2d(16, -67, Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-14, -6), Math.toRadians(315))
                .build();

        //Second Row From Gate
        Action pickUpBallsTwoP1 = drive.actionBuilder(new Pose2d(-14, -6, Math.toRadians(315)))
                .splineToLinearHeading(new Pose2d(10, -62, Math.toRadians(235)), Math.toRadians(250)) //unsure
                .build();
        Action pickUpBallsTwoP2 = drive.actionBuilder(new Pose2d(10, -62, Math.toRadians(235)))
                .strafeToLinearHeading(new Vector2d(-14, -6), Math.toRadians(270))
                .build();

        //First Row
        Action pickUpBallsThreeP1 = drive.actionBuilder(new Pose2d(-14, -6, Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-10, -55), Math.toRadians(270))
                .build();
        Action pickUpBallsThreeP2 = drive.actionBuilder(new Pose2d(-10, -55, Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-14, -6), Math.toRadians(315))
                .build();

        //Third Row
        Action pickUpBallsFourP1 = drive.actionBuilder(new Pose2d(-14, -6, Math.toRadians(315)))
                .strafeToLinearHeading(new Vector2d(40,-35), Math.toRadians(270))
                .build();
        Action pickUpBallsFourP2 = drive.actionBuilder(new Pose2d(40, -35, Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(40, -67), Math.toRadians(270))
                .build();
        Action pickUpBallsFourP3 = drive.actionBuilder(new Pose2d(40, -67, Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-14, -6), Math.toRadians(315))
                .build();

        //Leave Shooting Zone
        Action leaveShootingZone = drive.actionBuilder(new Pose2d(-14,-6,Math.toRadians(315)))
                .strafeToLinearHeading(new Vector2d(4, -55), Math.toRadians(0))
                .build();


        waitForStart();
        if (isStopRequested()) return;

        while(!isStopRequested() && opModeIsActive()) {
            //Start and shoot preload
            flywheel.flywheel.setVelocity(0.8*1600);
            sleep(500);
            intake.runIntake(1);
            turret.turnTo(-86);
            Actions.runBlocking(shootPreload);
            transfer.transferUp(1);
            spindexer.spindexer.setPower(0.135);
            sleep(2750);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.4);

            //Pick up balls from second row
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsOneP1,
                                    pickUpBallsOneP2
                            )
                    )
            );

            //Shoot balls that came from second row
            spindexer.spindexer.setPower(0.135);
            transfer.transferUp(1);
            sleep(2250);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.4);

            sleep(50000); //TEMPORARY STOP

            //Go to pick up balls from gate
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsTwoP1,
                                    pickUpBallsTwoP2
                            )
                    )
            );

            //Shoot balls that came from gate
            spindexer.spindexer.setPower(0.135);
            transfer.transferUp(1);
            sleep(2250);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.4);
            turret.turnTo(-45); //MAY BE NEGATIVE OR WRONG NUMBER

            //Pick up balls from first row
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsThreeP1,
                                    pickUpBallsThreeP2
                            )
                    )
            );

            //Shoot balls that came from first row
            spindexer.spindexer.setPower(0.135);
            transfer.transferUp(1);
            sleep(2250);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.4);
            turret.turnTo(-90); //MAY BE NEGATIVE OR WRONG NUMBER

            //Pick up balls from third row
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsFourP1,
                                    pickUpBallsFourP2,
                                    pickUpBallsFourP3
                            )
                    )
            );

            //Shoot balls that came from third row
            spindexer.spindexer.setPower(0.135);
            transfer.transferUp(1);
            sleep(2250);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0);
            intake.intake.setPower(0);

            //Leave shooting zone
            Actions.runBlocking(leaveShootingZone);

            sleep(50000);
    }
}}
