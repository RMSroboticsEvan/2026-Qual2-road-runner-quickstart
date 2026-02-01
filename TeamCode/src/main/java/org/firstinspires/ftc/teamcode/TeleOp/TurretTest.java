package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretV2;


@TeleOp
public class TurretTest extends LinearOpMode {
    private TurretV2 turret;
    private Limelight3A limelight;
    @Override
    public void runOpMode() throws InterruptedException {
        turret = new TurretV2(hardwareMap);
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");

        // pipeline 0 is a placeholder, put apriltag pipeline when configured
        limelight.pipelineSwitch(0);

        limelight.start();


        waitForStart();

        double kP = 0.015;
        double kD = 0.003;

        while (opModeIsActive()&& !isStopRequested()) {
            LLResult result = limelight.getLatestResult();

            if (result != null && result.isValid()) {
                double error = result.getTx();

                // deadband
                if (Math.abs(error) < 1.5) {
                    turret.turret.setPower(0);
                    continue;
                }

                double velocity = turret.turret.getVelocity();

                // scale down as we approach target
                double scale = Math.min(1.0, Math.abs(error) / 5.0);

                double power = (kP * error) - (kD * velocity);
                power *= scale;

                // velocity safety
                if (Math.abs(velocity) > 1200) {
                    power *= 0.5;
                }

                power = Math.max(-0.4, Math.min(0.4, power));

                turret.turret.setPower(-power);
            } else {
                turret.turret.setPower(0);
            }

            telemetry.addData("tx", result != null ? result.getTx() : "null");
            telemetry.addData("vel", turret.turret.getVelocity());
            telemetry.update();
        }


    }
}
