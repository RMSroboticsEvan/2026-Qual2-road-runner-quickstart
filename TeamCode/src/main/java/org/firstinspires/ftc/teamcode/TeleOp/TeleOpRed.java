package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Common.AllianceConfig;
import org.firstinspires.ftc.teamcode.Common.BaseTeleOp;

/**
 * TeleOp for Red Alliance.
 * All common logic is in BaseTeleOp - this class only provides Red-specific configuration.
 */
@TeleOp(name = "TeleOp Red")
public class TeleOpRed extends BaseTeleOp {
    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.forRed();
    }
}
