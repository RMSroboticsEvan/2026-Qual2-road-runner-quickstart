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
public class TeleOpRedNear extends LinearOpMode {

    private TurretV2 turretV2;
    private DriveTrain driveTrain;
    private Spindexer spindexer;
    private Intake intake;
    private Transfer transfer;
    private Flywheel flywheel;

    private double wrapAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    @Override
    public void runOpMode() throws InterruptedException {

        MecanumDrive drive = new MecanumDrive(hardwareMap, new Pose2d(8, 48, Math.toRadians(90)));
        driveTrain = new DriveTrain(hardwareMap);
        turretV2 = new TurretV2(hardwareMap);
        spindexer = new Spindexer(hardwareMap);
        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        flywheel = new Flywheel(hardwareMap);

        flywheel.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        PIDFCoefficients pidf = new PIDFCoefficients(150, 0, 0, 11.7025);
        flywheel.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        waitForStart();

        double r;
        double atan;
        double y = 62;

        double speed = 0.8;

        while (!isStopRequested() && opModeIsActive()) {

            driveTrain.driveMotors(gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);
            drive.updatePoseEstimate();
            Pose2d currentPose = drive.localizer.getPose();

//            if (gamepad2.squareWasPressed()) {
//                speed -= 0.025;
//            } else if (gamepad2.circleWasPressed()) {
//                speed += 0.025;
//            }

            if (currentPose.position.y < -15) {
                if (currentPose.position.x > 40) {
                    speed = 1.1; //Far shooting
                } else if (currentPose.position.x < -25) {
                    speed = 0.9; //Close shooting
                } else {
                    speed = 0.93; //Regular shooting
                }
            } else {
                if (currentPose.position.x > 40) {
                    speed = 1; //Far shooting
                } else if (currentPose.position.x < -25) {
                    speed = 0.8; //Close shooting
                } else {
                    speed = 0.83; //Regular shooting
                }
            }

            atan = Math.toDegrees(Math.atan(Math.abs(-63 - currentPose.position.x) / Math.abs(y - currentPose.position.y)));
            r = 90 + atan;

            double targetAngle = -Math.toDegrees(currentPose.heading.toDouble()) + r;
            targetAngle = wrapAngle(targetAngle);
            turretV2.turnTo(targetAngle);

            telemetry.addData("X", currentPose.position.x);
            telemetry.addData("Y", currentPose.position.y);
            telemetry.addData("atan", atan);
            telemetry.addData("Speed", speed);
            telemetry.addData("vel", flywheel.flywheel.getVelocity());
            telemetry.addData("Heading", Math.toDegrees(currentPose.heading.toDouble()));

            if(gamepad2.squareWasPressed()) {
                y = y-3;
            } else if (gamepad2.circleWasPressed()) {
                y = y+3;
            }


            if (gamepad2.crossWasPressed()) {
                drive.localizer.setPose(new Pose2d(4, 50, Math.toRadians(180)));
                y = 62;
            }
            if(gamepad2.triangleWasPressed()){
                y = 62;
            }

            if (gamepad1.left_stick_y == 0 &&
                    gamepad1.right_stick_x == 0 &&
                    gamepad1.left_stick_x == 0) {

                flywheel.flywheel.setVelocity(speed * 1600);
                driveTrain.stopMotor();
            } else {
                flywheel.flywheel.setPower(0.8);
            }

            if (gamepad2.right_bumper) {
                spindexer.spindexer.setPower(0.125);
                sleep(200);
                transfer.transferUp(1);
                intake.runIntake(1);
            } else if (gamepad2.left_bumper) {
                intake.runIntake(-1);
                spindexer.spindexer.setPower(-0.2);
            } else {
                spindexer.spindexer.setPower(0.28);
                intake.runIntake(1);
                transfer.transferDown(1);
            }

            //commit
            telemetry.update();
        }
    }
}