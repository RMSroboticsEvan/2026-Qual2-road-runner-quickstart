package org.firstinspires.ftc.teamcode.Autons;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Common.AllianceConfig;
import org.firstinspires.ftc.teamcode.Common.BaseAutonomousTopV2;

/**
 * Autonomous for Red Alliance - Top Starting Position
 * All common logic is in BaseAutonomousTopV2.
 * Coordinates are automatically mirrored for Red alliance.
 *
 * Features:
 * - Shoots preload ball
 * - Picks up and shoots 4 cycles of balls (second row, gate, first row, third row)
 * - Leaves shooting zone for parking
 */
@Autonomous(name = "Auto Red Top V2")
public class AutoRedTopV2 extends BaseAutonomousTopV2 {
    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.forRed();
    }
}
