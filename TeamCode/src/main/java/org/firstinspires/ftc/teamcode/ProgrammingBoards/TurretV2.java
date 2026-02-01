package org.firstinspires.ftc.teamcode.ProgrammingBoards;

import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
import com.acmerobotics.roadrunner.ftc.RawEncoder;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class TurretV2
{
    public Limelight3A limelight;

    public DcMotorEx turret;

    double kP = 0.03, kI=0.00025;
    double integral = 0;
    double prevTargetAngle = 0;
    public TurretV2(HardwareMap hardwareMap){
        limelight = hardwareMap.get(Limelight3A .class, "Limelight");

        // pipeline 0 is a placeholder, put apriltag pipeline when configured
        limelight.pipelineSwitch(0);
        turret = hardwareMap.get(DcMotorEx.class, "turret");
        turret.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        limelight.start();
    }


    private double toTicks(double angle)
    {
        // GoBILDA 435 RPM motor (28 CPR × 17.7 ratio = 495.6 ticks/rev)
        // Pulley ratio: 110/48 = 2.2917
        // Ticks per turret degree: 495.6 × 2.2917 / 360 = 3.155
        return angle * 3.15493055556;
    }
    private double toDegrees(double ticks)
    {
        return ticks / 3.15493055556;
    }



}
