package org.firstinspires.ftc.teamcode.ProgrammingBoards;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class LimelightDetection {
    public Limelight3A limelight;
    public LimelightDetection(HardwareMap hardwareMap){
        limelight = hardwareMap.get(Limelight3A.class, "Limelight");
    }

    public double getDistance(){
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {

            // --- Read Limelight values ---
            double ty = result.getTy(); // vertical angle in degrees
            double tx = result.getTx(); // horizontal angle in degrees
            double ta = result.getTa(); // target area in percent (small number, usually 0-5 for far targets)

            // --- Constants ---
            double cameraHeight = 17.0;       // floor to camera (inches)
            double targetHeight = 29.5;       // floor to Apriltag (inches)
            double heightDifference = targetHeight - cameraHeight; // 12.5 inches
            double cameraAngle = 0.0;         // mounting angle in degrees

            // --- Distance from ty ---
            double distanceTy = -1;
            if (ty != 0) {
                distanceTy = heightDifference / Math.tan(Math.toRadians(cameraAngle + ty));
            }
 
 	    // --- adjust distance based on horizontal angle 
            double distanceTxTy = -1;
            if (tx !=0 && distanceTy != -1) {
                distanceTxTy = distanceTy/Math.cos(Math.toRadians(tx));
            }

            // --- Distance from ta ---
            double distanceTa = -1;
            double taCalibrationConstant = 70.7; // recalibrated for taMeasured at 100 in
            if (ta > 0) {
                distanceTa = taCalibrationConstant / Math.sqrt(ta);
            }

            // --- Blending thresholds ---
            double blendStart = 45.0; // inches
            double blendEnd   = 65.0; // inches

            // --- Determine final distance ---
            double distanceInches;

            if (distanceTxTy > 0 && distanceTxTy < blendStart) {
                // Close range: use ty only
                distanceInches = distanceTxTy;
            } else if (distanceTa > 0 && distanceTxTy > blendEnd) {
                // Far range: use ta only
                distanceInches = distanceTa;
            } else if (distanceTxTy > 0 && distanceTa > 0) {
                // Blend region: linear interpolation
                double t = (distanceTxTy - blendStart) / (blendEnd - blendStart);
                t = Math.max(0, Math.min(1, t)); // clamp 0–1
                distanceInches = distanceTxTy * (1 - t) + distanceTa * t;
            } else {
                distanceInches = -1; // no valid measurement
            }

            // --- Telemetry ---
            if (distanceInches > 0) {
                return distanceInches;
            } else {
                return -1;
            }

        } else {
            return -1;
        }
    }
}
