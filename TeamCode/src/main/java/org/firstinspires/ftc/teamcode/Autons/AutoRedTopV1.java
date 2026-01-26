//package org.firstinspires.ftc.teamcode.Autons;
//
//import com.acmerobotics.roadrunner.Action;
//import com.acmerobotics.roadrunner.ParallelAction;
//import com.acmerobotics.roadrunner.Pose2d;
//import com.acmerobotics.roadrunner.SequentialAction;
//import com.acmerobotics.roadrunner.Vector2d;
//import com.acmerobotics.roadrunner.ftc.Actions;
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.hardware.DcMotor;
//
//import org.firstinspires.ftc.teamcode.MecanumDrive;
//import org.firstinspires.ftc.teamcode.ProgrammingBoards.DriveTrain;
//import org.firstinspires.ftc.teamcode.ProgrammingBoards.Flywheel;
//import org.firstinspires.ftc.teamcode.ProgrammingBoards.Intake;
//import org.firstinspires.ftc.teamcode.ProgrammingBoards.Spindexer;
//import org.firstinspires.ftc.teamcode.ProgrammingBoards.Transfer;
//import org.firstinspires.ftc.teamcode.ProgrammingBoards.Turret;
//
//@Autonomous
//public class AutoRedTopV1 extends LinearOpMode {
//    private DriveTrain driveTrain;
//    private Spindexer spindexer;
//    private Intake intake;
//    private Transfer transfer;
//    private Flywheel flywheel;
//    private Turret turret;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//        turret = new Turret (hardwareMap);
//        flywheel = new Flywheel (hardwareMap);
//        intake = new Intake (hardwareMap);
//        spindexer = new Spindexer (hardwareMap);
//        transfer = new Transfer (hardwareMap);
//        flywheel.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//
//        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(-48,48, Math.toRadians(135)));
//        //done
//        Action shotOne = drive.actionBuilder(new Pose2d(-48,48,Math.toRadians(135)))
//                .strafeToLinearHeading(new Vector2d(-10, 6), Math.toRadians(90))
//                .build();
//        //done
//        Action pickUpBallsOneP1 = drive.actionBuilder(new Pose2d(-10,6,Math.toRadians(90)))
//                .strafeToLinearHeading(new Vector2d(-10, 55), Math.toRadians(90))
//                .build();
//        //done
//        Action pickUpBallsOneP2NoGate = drive.actionBuilder(new Pose2d(-6,56,Math.toRadians(90)))
//                .strafeToLinearHeading(new Vector2d(-14, 6), Math.toRadians(45))
//                .build();
//        //done
//        Action pickUpBallsOneP2Gate = drive.actionBuilder(new Pose2d(-14,55,Math.toRadians(90)))
//                .strafeToLinearHeading(new Vector2d(4, 67), Math.toRadians(180))
//                .build();
//        //done
//        Action pickUpBallsOneP3Gate = drive.actionBuilder(new Pose2d(-8,60,Math.toRadians(180)))
//                .strafeToLinearHeading(new Vector2d(-14, 6), Math.toRadians(45))
//                .build();
//        //done
//
//        Action pickUpBallsTwoP1 = drive.actionBuilder(new Pose2d(-10,6,Math.toRadians(45)))
//                .strafeToLinearHeading(new Vector2d(16,35), Math.toRadians(90))
//                .build();
//        //done
//        Action pickUpBallsTwoP2 = drive.actionBuilder(new Pose2d(16,35,Math.toRadians(90)))
//                .strafeToLinearHeading(new Vector2d(16, 67), Math.toRadians(90))
//                .build();
//        //done
//        Action pickUpBallsTwoP3NoGate = drive.actionBuilder(new Pose2d(16,67,Math.toRadians(90)))
//                .strafeToLinearHeading(new Vector2d(-14, 6), Math.toRadians(45))
//                .build();
//        //done
//        Action pickUpBallsTwoP3Gate = drive.actionBuilder(new Pose2d(16,67,Math.toRadians(90)))
//                .strafeToLinearHeading(new Vector2d(4, 40), Math.toRadians(180))
//                .build();
//        //done
//        Action pickUpBallsTwoP4Gate = drive.actionBuilder(new Pose2d(4,40,Math.toRadians(180)))
//                .strafeToLinearHeading(new Vector2d(4, 60), Math.toRadians(180))
//                .build();
//        //done
//        Action pickUpBallsTwoP5Gate = drive.actionBuilder(new Pose2d(4,60, Math.toRadians(180)))
//                .strafeToLinearHeading(new Vector2d(-14, 6), Math.toRadians(45))
//                .build();
//        //done
//
//        Action pickUpBallsThreeP1 = drive.actionBuilder(new Pose2d(-14,6,Math.toRadians(45)))
//                .strafeToLinearHeading(new Vector2d(40,35), Math.toRadians(90))
//                .build();
//        //done
//        Action pickUpBallsThreeP2 = drive.actionBuilder(new Pose2d(40,35,Math.toRadians(90)))
//                .strafeToLinearHeading(new Vector2d(40, 67), Math.toRadians(90))
//                .build();
//        //done
//        Action pickUpBallsThreeP3 = drive.actionBuilder(new Pose2d(40,67,Math.toRadians(90)))
//                .strafeToLinearHeading(new Vector2d(-14, 6), Math.toRadians(45))
//                .build();
//        //done
//
//        Action autoPark = drive.actionBuilder(new Pose2d(-14,6,Math.toRadians(45)))
//                .strafeToLinearHeading(new Vector2d(30, 32.5), Math.toRadians(180))
//                .build();
//
//
//        waitForStart();
//        if (isStopRequested()) return;
//
//        while(!isStopRequested() && opModeIsActive()) {
//            flywheel.flywheel.setVelocity(0.76*1600);
//            spindexer.spindexer.setPower(0.125);
//            transfer.transferDown(1);
//            sleep(500);
//            intake.runIntake(1);
//            turret.TurnToAuto(23);
//            Actions.runBlocking(shotOne);
//            transfer.transferUp(1);
//            sleep(2750);
//            transfer.transferDown(1);
//            turret.TurnToAuto(65);
//            spindexer.spindexer.setPower(0.25);
//            Actions.runBlocking(
//                    new ParallelAction(
//                            new SequentialAction(
//                                    pickUpBallsOneP1,
//                                    pickUpBallsOneP2NoGate
//                            )
//                    )
//            );
//            flywheel.flywheel.setVelocity(0.74*1600);
//            spindexer.spindexer.setPower(0.125);
//            transfer.transferUp(1);
//            sleep(2250);
//            transfer.transferDown(1);
//            spindexer.spindexer.setPower(0.2);
//            turret.TurnToAuto(81.5);
//            Actions.runBlocking(
//                    new ParallelAction(
//                            new SequentialAction(
//                                    pickUpBallsTwoP1,
//                                    pickUpBallsTwoP2,
//                                    pickUpBallsTwoP3NoGate
//                            )
//                    )
//            );
//
//            spindexer.spindexer.setPower(0.125);
//            sleep(100);
//            transfer.transferUp(1);
//            sleep(2600);
//            transfer.transferDown(1);
//            spindexer.spindexer.setPower(0.25);
//            turret.TurnToAuto(85);
//            Actions.runBlocking(
//                    new ParallelAction(
//                            new SequentialAction(
//                                    pickUpBallsThreeP1,
//                                    pickUpBallsThreeP2,
//                                    pickUpBallsThreeP3
//                            )
//                    )
//            );
//            spindexer.spindexer.setPower(0.125);
//            transfer.transferUp(1);
//            sleep(2750);
//            transfer.transferDown(1);
//            spindexer.spindexer.setPower(0.25);
//            Actions.runBlocking(autoPark);
//            sleep(50000);
//        }
//    }
//}
