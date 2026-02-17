package org.firstinspires.ftc.teamcode.ProgrammingBoards;

import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.List;

/**
 * TurretHardware - Low-level hardware interface for turret control
 *
 * This is the hardware abstraction layer for the turret subsystem.
 * Controls the physical servo and Limelight, provides basic aiming methods.
 *
 * Hardware:
 * - Servo: "axon" (positional servo, 0-1 range)
 * - Limelight 3A: Vision system for AprilTag detection
 *
 * Used by: TurretController (high-level intelligent aiming)
 */
public class TurretHardware {
    public Limelight3A limelight;
    public Servo turret;
    public TurretHardware(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");
        limelight.pipelineSwitch(0);
        turret = hardwareMap.get(Servo.class, "axon");

        limelight.start();

    }
    double conversion = 360/0.513;
    public void turnTo(double degrees){
        double ticks = degrees/conversion;
        turret.setPosition(ticks+0.505);

    }
    double xAngle = 0;

    private static final double DEADBAND = 1.0; // degrees

    double currAngle = 0;
    public void TurnToAT() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return;

        double tx = result.getTx();

        if (Math.abs(tx) < DEADBAND) return;

        currAngle = currAngle - (tx * conversion);
        turnTo(currAngle);
    }
    public double returnTx(){
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()){
            return result.getTx();
        }
        else{
            return 1000;
        }



    }


}
