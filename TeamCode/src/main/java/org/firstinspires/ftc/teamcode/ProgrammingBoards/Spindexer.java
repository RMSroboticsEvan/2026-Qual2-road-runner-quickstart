package org.firstinspires.ftc.teamcode.ProgrammingBoards;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.hardware.rev.RevTouchSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Spindexer {
    public DcMotorEx spindexer;
    RevColorSensorV3 colorSensorV3;

    public RevTouchSensor touchSensor;
    public DistanceSensor distanceSensor;

    Boolean acceptingBalls = true;


    public Spindexer(HardwareMap hardwareMap){
        spindexer = hardwareMap.get(DcMotorEx.class, "spindexer");
        spindexer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        colorSensorV3 = hardwareMap.get(RevColorSensorV3.class, "colorSensor");
        touchSensor = hardwareMap.get(RevTouchSensor.class, "touchSensor");
        distanceSensor = hardwareMap.get(DistanceSensor.class, "distanceSensor");
        spindexer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public int getBlueResult(){
        return(colorSensorV3.blue());
    }
    public int getRedResult(){
        return(colorSensorV3.red());
    }
    public int getGreenResult(){
        return(colorSensorV3.green());
    }

    public int ballCount = 0;
    double currentPos = 0;
    ElapsedTime ballTimer = new ElapsedTime();
    public void checkIfBall(){
        if(ballCount == 3){
            acceptingBalls = false;
        }else if (ballCount == 0){
            acceptingBalls = true;
        }
        if(acceptingBalls){
            if ((colorSensorV3.red() > 250 || colorSensorV3.blue() > 250 || colorSensorV3.green() > 250) && !spindexer.isBusy() && ballCount < 3) {
                rotateThird();
                ballCount++;
            }
        }else if(!acceptingBalls){
            if ((distanceSensor.getDistance(DistanceUnit.MM)> 200 && distanceSensor.getDistance(DistanceUnit.MM) < 250) && ballCount >= 1 && ballTimer.milliseconds() > 300) {
                ballCount--;
                ballTimer.reset();
            }
        }

    }
    public void resetBallCount(){
        ballCount =0;
    }



    public int toDegree(double ticks){
        return (int) (ticks/3.9583333333);
    }
    public int toTicks(double angle){
        return (int) (angle*3.9583333333);
    }


    public int targetAngleToTargetTicks(double angle)
    {
        int currentPos = spindexer.getCurrentPosition();
        double currentPosAdjustedDegrees = toDegree(currentPos) % 360;
        double error = angle - currentPosAdjustedDegrees;
        return currentPos + toTicks(error);
    }
    public void switchAcceptingMode(){
        acceptingBalls = !acceptingBalls;
    }


    public void toAngle(int angle){
        spindexer.setTargetPosition(targetAngleToTargetTicks(angle));
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);

    }
    public void switchModes(){
        spindexer.setPower(1);
        toAngle((toDegree(spindexer.getCurrentPosition())%360) - 60);

    }
    public void rotateThird(){
        spindexer.setPower(0.8);
        toAngle((toDegree(spindexer.getCurrentPosition())%360) - 120);

    }
    public void getPos(){
        if(touchSensor.isPressed()){
            currentPos = 0;
        }

    }

    public boolean returnShootingMode(){
        return acceptingBalls;
    }




    //1425 ticks per full rotation
    //3.9583333333 ticks per degree
}
