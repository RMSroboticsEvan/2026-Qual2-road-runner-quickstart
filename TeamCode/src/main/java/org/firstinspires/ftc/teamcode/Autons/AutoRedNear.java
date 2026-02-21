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
public class AutoRedNear extends LinearOpMode {
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

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(-51,49, Math.toRadians(135)));

        //Shoot Preload
        Action shootPreload = drive.actionBuilder(new Pose2d(-51,49,Math.toRadians(135)))
                .strafeToLinearHeading(new Vector2d(-10, 6), Math.toRadians(45))
                .build();

        //Second Row
        Action pickUpBallsOneP1 = drive.actionBuilder(new Pose2d(-10, 6, Math.toRadians(45)))
                .splineToSplineHeading(new Pose2d(16, 25, Math.toRadians(90)), Math.toRadians(95))
                .build();
        Action pickUpBallsOneP2 = drive.actionBuilder(new Pose2d(16, 25, Math.toRadians(90)))
                .splineToSplineHeading(new Pose2d(16, 60, Math.toRadians(90)), Math.toRadians(90))
                .build();
        Action pickUpBallsOneP3 = drive.actionBuilder(new Pose2d(16, 60, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(16, 25), Math.toRadians(90))
                .build();
        Action pickUpBallsOneP4 = drive.actionBuilder(new Pose2d(16, 25, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-16, 6), Math.toRadians(45))
                .build();

        //Second Row From Gate
        Action pickUpBallsTwoP1 = drive.actionBuilder(new Pose2d(-16, 6, Math.toRadians(45)))
                .splineToLinearHeading(new Pose2d(10, 62, Math.toRadians(125)), Math.toRadians(70))
                .build();
        Action pickUpBallsTwoP2 = drive.actionBuilder(new Pose2d(10, 62, Math.toRadians(125)))
                .strafeToLinearHeading(new Vector2d(10, 30), Math.toRadians(90))
                .build();
        Action pickUpBallsTwoP3 = drive.actionBuilder(new Pose2d(10, 30, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-22, 6), Math.toRadians(90))
                .build();

        //First Row
        Action pickUpBallsThreeP1 = drive.actionBuilder(new Pose2d(-22, 6, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-29, 52), Math.toRadians(90))
                .build();
        Action pickUpBallsThreeP2 = drive.actionBuilder(new Pose2d(-29, 52, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-16, 6), Math.toRadians(45))
                .build();

        //Third Row
        Action pickUpBallsFourP1 = drive.actionBuilder(new Pose2d(-16, 6, Math.toRadians(45)))
                .strafeToLinearHeading(new Vector2d(40,35), Math.toRadians(90))
                .build();
        Action pickUpBallsFourP2 = drive.actionBuilder(new Pose2d(40, 35, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(40, 67), Math.toRadians(90))
                .build();
        Action pickUpBallsFourP3 = drive.actionBuilder(new Pose2d(40, 67, Math.toRadians(90)))
                .strafeToLinearHeading(new Vector2d(-14, 6), Math.toRadians(45))
                .build();

        //Leave Shooting Zone
        Action leaveShootingZone = drive.actionBuilder(new Pose2d(-16,6,Math.toRadians(45)))
                .splineToSplineHeading(new Pose2d(3.5, 50, Math.toRadians(90)), Math.toRadians(90))
                .build();


        waitForStart();
        if (isStopRequested()) return;

        while(!isStopRequested() && opModeIsActive()) {
            //Start and shoot preload
            flywheel.flywheel.setVelocity(0.765*1600);
            intake.runIntake(1);
            turret.turnTo(87);
            Actions.runBlocking(shootPreload);
            transfer.transferUp(1);
            spindexer.spindexer.setPower(0.165);
            sleep(2750);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.225);

            //Pick up balls from second row
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsOneP1,
                                    pickUpBallsOneP2,
                                    pickUpBallsOneP3,
                                    pickUpBallsOneP4
                            )
                    )
            );

            //Shoot balls that came from second row
            flywheel.flywheel.setVelocity(0.76*1600);
            turret.turnTo(91);
            spindexer.spindexer.setPower(0.155);
            transfer.transferUp(1);
            sleep(2500);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.225);

            //Go to pick up balls from gate
            Actions.runBlocking(pickUpBallsTwoP1);
            flywheel.flywheel.setVelocity(0.765*1600);
            turret.turnTo(51);
            sleep(700);
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsTwoP2,
                                    pickUpBallsTwoP3
                            )
                    )
            );

            //Shoot balls that came from gate
            spindexer.spindexer.setPower(0.165);
            transfer.transferUp(1);
            sleep(2250);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.225);


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
            turret.turnTo(92);
            sleep(200);
            spindexer.spindexer.setPower(0.155);
            transfer.transferUp(1);
            sleep(2250);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(0.225);

//            sleep(50000); //TEMPORARY STOP
//
//            //Pick up balls from third row
//            Actions.runBlocking(
//                    new ParallelAction(
//                            new SequentialAction(
//                                    pickUpBallsFourP1,
//                                    pickUpBallsFourP2,
//                                    pickUpBallsFourP3
//                            )
//                    )
//            );
//
//            //Shoot balls that came from third row
//            spindexer.spindexer.setPower(0.157);
//            transfer.transferUp(1);
//            sleep(2250);
//            transfer.transferDown(1);
//            spindexer.spindexer.setPower(0);
//            intake.intake.setPower(0);

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
