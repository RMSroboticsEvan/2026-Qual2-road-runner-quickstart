//package org.firstinspires.ftc.teamcode.ProgrammingBoards;
//
//import com.acmerobotics.roadrunner.ftc.OverflowEncoder;
//import com.acmerobotics.roadrunner.ftc.RawEncoder;
//import com.qualcomm.hardware.limelightvision.LLResult;
//import com.qualcomm.hardware.limelightvision.LLResultTypes;
//import com.qualcomm.hardware.limelightvision.Limelight3A;
//import com.qualcomm.robotcore.hardware.CRServo;
//import com.qualcomm.robotcore.hardware.DcMotorEx;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//
//import java.util.List;
//
//public class Turret
//{
//    public Limelight3A limelight;
//    public CRServo axon;
//    public OverflowEncoder turretEncoder;
//    double kP = 0.03, kI=0.00025;
//    double integral = 0;
//    double prevTargetAngle = 0;
//
//    public Turret(HardwareMap hardwareMap){
//        limelight = hardwareMap.get(Limelight3A .class, "Limelight");
//
//        // pipeline 0 is a placeholder, put apriltag pipeline when configured
//        limelight.pipelineSwitch(0);
//
//        limelight.start();
//        axon = hardwareMap.get(CRServo.class, "axon");
//        turretEncoder = new OverflowEncoder(new RawEncoder(hardwareMap.get(DcMotorEx.class, "rightBack")));
//    }
//
//    private double toTicks(double angle)
//    {
//        return angle * 29.7770061728;
//    }
//    private double toDegrees(double ticks)
//    {
//        return ticks/29.7770061728;
//    }
//    public void TurnToAuto(double angle){
//
//        double error = 0;
//        do{
//            if (Math.abs(angle - prevTargetAngle) > 20) {
//                integral = 0;
//            }
//
//            double currAngle = toDegrees(turretEncoder.encoder.getPositionAndVelocity().position);
//            error = angle - currAngle;
//            integral += error;
//
//            double output = kP * error + kI * integral;
//
//
//            axon.setPower(output);
//
//            prevTargetAngle = angle;
//        } while(Math.abs(error) > 2);
//        axon.setPower(0);
//
//
//
//    }
//    public void TurnTo(double angle){
//        if(Math.abs(angle - prevTargetAngle) > 20)
//        {
//            integral = 0;
//        }
//
//        double currAngle = toDegrees(turretEncoder.encoder.getPositionAndVelocity().position);
//        double error = angle-currAngle;
//        integral += error;
//
//        double output = kP*error + kI * integral;
//
//
//
//
//        axon.setPower(output);
//
//        prevTargetAngle = angle;
//
//    }
//    double xAngle;
//    public void TurnToAT(double tag){
//        double currAngle = toDegrees(turretEncoder.encoder.getPositionAndVelocity().position);
//        LLResult result = limelight.getLatestResult();
//
//        if (result != null) {
//
//            if (result.isValid()) {
//                List<LLResultTypes.FiducialResult> results = result.getFiducialResults();
//                //get limelight values:
//                xAngle = 0;
//                for(int i = 0; i < results.size(); i++){
//                    if(results.get(i).getFiducialId() == tag){
//                        xAngle = results.get(i).getTargetXDegrees();
//                    }
//                }
//
//                double targetAngle = currAngle - xAngle;
//
//                TurnTo(targetAngle);
//            }
//            else
//            {
//                TurnTo(currAngle);
//            }
//        }
//                else
//        {
//            TurnTo(currAngle);
//        }
//
//    }
//
//
//    public  double getCurrAngle(){
//        return toDegrees(turretEncoder.encoder.getPositionAndVelocity().position);
//    }
//    public double getTx(){
//        return xAngle;
//    }
//
//}
