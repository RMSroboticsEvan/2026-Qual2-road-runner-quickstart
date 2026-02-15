package org.firstinspires.ftc.teamcode.TeleOp;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.MecanumDrive;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.DriveTrain;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Flywheel;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Intake;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Spindexer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Transfer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretV2;

@TeleOp
public class TeleOpCoordinates extends LinearOpMode {

    private TurretV2 turretV2;
    private DriveTrain driveTrain;

    private Spindexer spindexer;
    private Intake intake;
    private Transfer transfer;

    private Flywheel flywheel;
    @Override
    public void runOpMode() throws InterruptedException {
        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(0, 0, Math.toRadians(180)));
        driveTrain = new DriveTrain(hardwareMap);

        turretV2 = new TurretV2(hardwareMap);

        spindexer = new Spindexer(hardwareMap);
        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        flywheel = new Flywheel(hardwareMap);

        flywheel.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double speed = 0.85;

        PIDFCoefficients pidf = new PIDFCoefficients(150,0,0,11.7025);
        flywheel.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);
        waitForStart();

        double r;

        while (!isStopRequested() && opModeIsActive()) {
            driveTrain.driveMotors(gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
            drive.updatePoseEstimate();

            Pose2d currentPose = drive.localizer.getPose();
            if(gamepad2.squareWasPressed()){
                speed = speed - 0.025;
            } else if (gamepad2.circleWasPressed()) {
                speed = speed + 0.025;
            }
            r = 90 + Math.toDegrees(Math.atan(Math.abs(-63-currentPose.position.x)/Math.abs(58-currentPose.position.y)));
            turretV2.turnTo(-Math.toDegrees(currentPose.heading.toDouble()) + r);

            telemetry.addData("X", currentPose.position.x);
            telemetry.addData("Y", currentPose.position.y);
            telemetry.addData("atan", Math.toDegrees(Math.atan(Math.abs(-63-currentPose.position.x)/Math.abs(58-currentPose.position.y))));
            telemetry.addData("Heading", Math.toDegrees(currentPose.heading.toDouble()));

            if (gamepad1.crossWasPressed()) {
                drive.localizer.setPose(new Pose2d(59,-59, Math.toRadians(0)));
            }

            if (gamepad1.left_stick_y == 0 && gamepad1.right_stick_x == 0 && gamepad1.left_stick_x == 0){
                flywheel.flywheel.setVelocity(speed*1600);
                driveTrain.stopMotor();
            }else{
                flywheel.flywheel.setPower(0.8);
            }



            //INTAKE


            if(gamepad2.right_bumper){
                spindexer.spindexer.setPower(0.125);
                sleep(200);
                transfer.transferUp(1);
                intake.runIntake(1);
            }else if(gamepad2.left_bumper){
                intake.runIntake(-1);
                spindexer.spindexer.setPower(-0.2);
            }
            else{
                spindexer.spindexer.setPower(0.5);
                intake.runIntake(1);
                transfer.transferDown(1);
            }

            telemetry.update();
        }
    }
}
