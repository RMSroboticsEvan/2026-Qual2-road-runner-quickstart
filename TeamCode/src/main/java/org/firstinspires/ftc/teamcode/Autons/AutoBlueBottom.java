package org.firstinspires.ftc.teamcode.Autons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Common.AllianceConfig;
import org.firstinspires.ftc.teamcode.Common.BaseAutonomousBottom;

/**
 * Autonomous program for Blue Alliance starting from BOTTOM position.
 *
 * This extends BaseAutonomousBottom which handles:
 * - Hardware initialization
 * - Coordinate system (no mirroring for Blue)
 * - Alliance-specific parameters
 *
 * TODO: Define starting coordinates for Blue Bottom position
 * TODO: Implement autonomous strategy for Bottom position
 * TODO: Set useAutoAlign if turret auto-align desired
 */
@Autonomous(name = "Auto Blue Bottom", group = "Blue")
public class AutoBlueBottom extends BaseAutonomousBottom {
    @Override
    protected AllianceConfig getAllianceConfig() {
        // TODO: Adjust starting coordinates for BOTTOM position
        // Current values are copied from Top - need to set Bottom position
        return AllianceConfig.forBlue();
    }
}
