package org.firstinspires.ftc.teamcode.Autons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Common.AllianceConfig;
import org.firstinspires.ftc.teamcode.Common.BaseAutonomousBottom;

/**
 * Autonomous program for Red Alliance starting from BOTTOM position.
 *
 * This extends BaseAutonomousBottom which handles:
 * - Hardware initialization
 * - Coordinate mirroring (Y-axis flip for Red alliance)
 * - Alliance-specific parameters
 *
 * TODO: Define starting coordinates for Red Bottom position
 * TODO: Implement autonomous strategy for Bottom position
 * TODO: Set useAutoAlign if turret auto-align desired
 */
@Autonomous(name = "Auto Red Bottom", group = "Red")
public class AutoRedBottom extends BaseAutonomousBottom {
    @Override
    protected AllianceConfig getAllianceConfig() {
        // TODO: Adjust starting coordinates for BOTTOM position
        // Current values are copied from Top - need to set Bottom position
        // Coordinates will automatically mirror for Red alliance
        return AllianceConfig.forRed();
    }
}
