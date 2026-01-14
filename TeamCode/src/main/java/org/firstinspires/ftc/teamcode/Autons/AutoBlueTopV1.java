package org.firstinspires.ftc.teamcode.Autons;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.DriveTrain;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Flywheel;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Intake;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Spindexer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Transfer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Turret;

@Autonomous
public class AutoBlueTopV1 extends LinearOpMode {
    private DriveTrain driveTrain;
    private Spindexer spindexer;
    private Intake intake;
    private Transfer transfer;
    private Flywheel flywheel;
    private Turret turret;

    @Override
    public void runOpMode() throws InterruptedException {
        turret = new Turret (hardwareMap);
        flywheel = new Flywheel (hardwareMap);
        intake = new Intake (hardwareMap);
        spindexer = new Spindexer (hardwareMap);
        transfer = new Transfer (hardwareMap);

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(-48,-48, Math.toRadians(225)));

        Action shotOne = drive.actionBuilder(new Pose2d(-48,-48,Math.toRadians(225)))
                .strafeToLinearHeading(new Vector2d(-8, -8), Math.toRadians(270))
                .build();
        Action pickUpBallsOneP1 = drive.actionBuilder(new Pose2d(-8,-8,Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-8, -50), Math.toRadians(270))
                .build();
        Action pickUpBallsOneP2 = drive.actionBuilder(new Pose2d(-8,-50,Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-8, -8), Math.toRadians(315))
                .build();
        Action pickUpBallsTwoP1 = drive.actionBuilder(new Pose2d(-8,-8,Math.toRadians(315)))
                .splineToLinearHeading(new Pose2d(14,-60, Math.toRadians(270)), Math.toRadians(270))
                .build();
        Action pickUpBallsTwoP2 = drive.actionBuilder(new Pose2d(14,-60,Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-8, -8), Math.toRadians(315))
                .build();
        Action pickUpBallsThreeP1 = drive.actionBuilder(new Pose2d(-8,-8,Math.toRadians(315)))
                .splineToLinearHeading(new Pose2d(40,-60, Math.toRadians(270)), Math.toRadians(270))
                .build();
        Action pickUpBallsThreeP2 = drive.actionBuilder(new Pose2d(14,-60,Math.toRadians(270)))
                .strafeToLinearHeading(new Vector2d(-8, -8), Math.toRadians(245))
                .build();


        waitForStart();
        if (isStopRequested()) return;

        while(!isStopRequested() && opModeIsActive()) {
            int count = 0;
            flywheel.runFlywheelVel(0.8);
            intake.runIntake(1);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(-0.7);
            //turret.TurnToAuto(-17);
            //Actions.runBlocking(shotOne);
//            while(count < 100){
//                if(spindexer.touchSensor.isPressed()){
//                    spindexer.spindexer.setPower(0);
//                    transfer.transferUp(1);
//                    sleep(600);
//                    spindexer.spindexer.setPower(-0.7);
//                    count++;
//                }else{
//                    transfer.transferDown(1);
//                    spindexer.spindexer.setPower(-0.7);
//                }
//            }
            spindexer.spindexer.setPower(0.15
            );
            transfer.transferUp(1);
            count = 0;
            sleep(30000);
            transfer.transferDown(1);
            spindexer.spindexer.setPower(-0.7);
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsOneP1,
                                    pickUpBallsOneP2
                            )
                    )
            );
            turret.TurnToAuto(-62);
            transfer.transferUp(1);
            sleep(8000);
//            while(count < 3){
//                if(spindexer.touchSensor.isPressed()){
//                    spindexer.spindexer.setPower(0);
//                    sleep(100);
//                    transfer.transferUp(1);
//                    sleep(600);
//                    spindexer.spindexer.setPower(-0.7);
//                    count++;
//                }else{
//                    transfer.transferDown(1);
//                    spindexer.spindexer.setPower(-0.7);
//                }
//            }
//            count = 0;
            transfer.transferDown(1);
            spindexer.spindexer.setPower(-0.7);
            sleep(30000);//REMOVEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsTwoP1,
                                    pickUpBallsTwoP2
                            )
                    )
            );
//            while(count < 3){
//                if(spindexer.touchSensor.isPressed()){
//                    spindexer.spindexer.setPower(0);
//                    sleep(100);
//                    transfer.transferUp(1);
//                    sleep(600);
//                    spindexer.spindexer.setPower(-0.7);
//                    count++;
//                }else{
//                    transfer.transferDown(1);
//                    spindexer.spindexer.setPower(-0.7);
//                }
//            }
//            count = 0;
            transfer.transferDown(1);
            spindexer.spindexer.setPower(-0.7);
            Actions.runBlocking(
                    new ParallelAction(
                            new SequentialAction(
                                    pickUpBallsThreeP1,
                                    pickUpBallsThreeP2
                            )
                    )
            );
//            while(count < 3){
//                if(spindexer.touchSensor.isPressed()){
//                    spindexer.spindexer.setPower(0);
//                    sleep(100);
//                    transfer.transferUp(1);
//                    sleep(600);
//                    spindexer.spindexer.setPower(-0.7);
//                    count++;
//                }else{
//                    transfer.transferDown(1);
//                    spindexer.spindexer.setPower(-0.7);
//                }
//            }
//            count = 0;
            transfer.transferDown(0);
            spindexer.spindexer.setPower(0);
            sleep(30000);
        }
    }
}
