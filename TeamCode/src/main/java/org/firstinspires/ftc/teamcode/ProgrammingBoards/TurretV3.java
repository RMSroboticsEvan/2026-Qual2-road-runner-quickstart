package org.firstinspires.ftc.teamcode.ProgrammingBoards;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class TurretV3 {

    public Limelight3A limelight;
    public Servo turret;


    // Servo position that corresponds to 0 degrees (straight ahead)
    private static final double CENTER_POSITION = 0.505;

    private static final double SERVO_RANGE_DEGREES = 180.0;

    // Servo hard limits
    private static final double MIN_POSITION = 0.0;
    private static final double MAX_POSITION = 1.0;


    private static final double KP = 0.6;        // proportional gain (0-1, percentage of correction per loop)
    private static final double DEADBAND = 1.0;  // degrees


    // Track last commanded servo position
    private double currentTargetPosition = CENTER_POSITION;


    public TurretV3(HardwareMap hardwareMap) {
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");
        turret = hardwareMap.get(Servo.class, "axon");

        limelight.pipelineSwitch(0);
        limelight.start();

        // Initialize turret to center
        turret.setPosition(CENTER_POSITION);
        currentTargetPosition = CENTER_POSITION;
    }


    public void turnTo(double degrees) {
        double servoPos = CENTER_POSITION + (degrees / SERVO_RANGE_DEGREES);

        servoPos = clamp(servoPos);

        currentTargetPosition = servoPos;
        turret.setPosition(currentTargetPosition);
    }


    public void TurnToAT() {
        LLResult result = limelight.getLatestResult();
        if (result == null || !result.isValid()) return;

        double tx = result.getTx(); // degrees offset from target

        // Stop when aligned
        if (Math.abs(tx) < DEADBAND) return;

        // Convert degrees to servo position, then apply proportional gain
        double correction = (tx / SERVO_RANGE_DEGREES) * KP;

        currentTargetPosition -= correction;
        currentTargetPosition = clamp(currentTargetPosition);

        turret.setPosition(currentTargetPosition);
    }

    public double getCurrentTargetPosition() {
        return currentTargetPosition;
    }

    public double getCurrentTargetAngle() {
        return (currentTargetPosition - CENTER_POSITION) * SERVO_RANGE_DEGREES;
    }

    private double clamp(double value) {
        return Math.max(MIN_POSITION, Math.min(MAX_POSITION, value));
    }
}
