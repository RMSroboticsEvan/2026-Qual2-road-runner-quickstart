package org.firstinspires.ftc.teamcode.Autons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Common.AllianceConfig;
import org.firstinspires.ftc.teamcode.Common.BaseAutonomousTop;

/**
 * Autonomous for Blue Alliance - Top Starting Position
 * All common logic is in BaseAutonomousTop.
 *
 * Features:
 * - Shoots preload ball
 * - Picks up and shoots 4 cycles of balls (second row, gate, first row, third row)
 * - Leaves shooting zone for parking
 */
@Autonomous(name = "Auto Blue Top")
public class AutoBlueTop extends BaseAutonomousTop {
    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.forBlue();
    }
}
