package org.firstinspires.ftc.teamcode.ProgrammingBoards;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Transfer {
    public DcMotorEx transfer;
    public Transfer(HardwareMap hardwareMap){
        transfer = hardwareMap.get(DcMotorEx.class, "transfer");
        transfer.setDirection(DcMotorSimple.Direction.REVERSE);
    }
    public void transferUp(double pwr){
        transfer.setPower(-pwr);
    }
    public void transferDown(double pwr){
        transfer.setPower(pwr);
    }

}
