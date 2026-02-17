package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Common.AllianceConfig;
import org.firstinspires.ftc.teamcode.Common.BaseTeleOp;

/**
 * TeleOp for Blue Alliance.
 * All common logic is in BaseTeleOp - this class only provides Blue-specific configuration.
 */
@TeleOp(name = "TeleOp Blue")
public class TeleOpBlue extends BaseTeleOp {
    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.forBlue();
    }
}
