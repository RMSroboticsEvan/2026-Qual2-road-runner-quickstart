package org.firstinspires.ftc.teamcode.TeleOp;

import android.util.Log;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.DriveTrain;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Flywheel;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Intake;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Spindexer;
import org.firstinspires.ftc.teamcode.ProgrammingBoards.Transfer;
import org.firstinspires.ftc.teamcode.util.CSVLogger;



@TeleOp
public class TeleOpBlue extends LinearOpMode {
    private DriveTrain driveTrain;
    private Spindexer spindexer;
    private Intake intake;
    private Transfer transfer;

    private Flywheel flywheel;



    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize FTC Dashboard
        FtcDashboard dashboard = FtcDashboard.getInstance();
        ElapsedTime runtime = new ElapsedTime();

        // Initialize CSV Logger for Tableau export
        CSVLogger csvLogger = new CSVLogger("teleop_blue");
        csvLogger.writeHeader("Time", "Voltage", "FlywheelVel", "FlywheelTarget",
                             "SpindexerPos", "SpindexerPower");

        driveTrain = new DriveTrain(hardwareMap);
        spindexer = new Spindexer(hardwareMap);
        intake = new Intake(hardwareMap);
        transfer = new Transfer(hardwareMap);
        flywheel = new Flywheel(hardwareMap);
        flywheel.flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        double speed = 0.85;

        // Get voltage sensor
        VoltageSensor voltageSensor = hardwareMap.voltageSensor.iterator().next();

        PIDFCoefficients pidf = new PIDFCoefficients(150,0,0,11.7025);
        flywheel.flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, pidf);

        waitForStart();
        runtime.reset();

        while (!isStopRequested() && opModeIsActive()) {
            // Create telemetry packet for Dashboard
            TelemetryPacket packet = new TelemetryPacket();

            // Get voltage
            double voltage = voltageSensor.getVoltage();

            // Regular telemetry (Driver Station)
            telemetry.addData("current", spindexer.spindexer.getCurrentPosition());
            telemetry.addData("target", spindexer.spindexer.getTargetPosition());
            telemetry.addData("vel", flywheel.returnVel());
            telemetry.addData("balls", spindexer.ballCount);
            telemetry.addData("voltage", "%.2f V", voltage);

            // FTC Dashboard telemetry (for graphing and export)
            packet.put("Time", runtime.seconds());
            packet.put("Battery Voltage", voltage);
            packet.put("Flywheel Velocity", flywheel.returnVel());
            packet.put("Flywheel Target", speed * 1600);
            packet.put("Spindexer Position", spindexer.spindexer.getCurrentPosition());
            packet.put("Spindexer Power", spindexer.spindexer.getPower());

            // Send to dashboard
            dashboard.sendTelemetryPacket(packet);

            // Log to CSV for Tableau export
            csvLogger.writeRow(
                String.format("%.3f", runtime.seconds()),
                String.format("%.3f", voltage),
                String.format("%.1f", flywheel.returnVel()),
                String.format("%.1f", speed * 1600),
                spindexer.spindexer.getCurrentPosition(),
                String.format("%.3f", spindexer.spindexer.getPower())
            );




            //DRIVETRAIN
            driveTrain.driveMotors(gamepad1.left_stick_y, gamepad1.right_stick_x, gamepad1.left_stick_x);



            //FLYWHEEL
            if(gamepad2.squareWasPressed()){
                speed = speed - 0.025;
            } else if (gamepad2.circleWasPressed()) {
                speed = speed + 0.025;
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





            //TRANSFER



            //BALLCOUNT
            //spindexer.checkIfBall();

            //RESET BALL COUNT


            //TURRET AUTO ALIGN

            telemetry.update();

        }

        // Close CSV logger when OpMode ends
        csvLogger.close();
        telemetry.addData("CSV Export", "Saved to /sdcard/FIRST/data/");
        telemetry.update();

    }
}
