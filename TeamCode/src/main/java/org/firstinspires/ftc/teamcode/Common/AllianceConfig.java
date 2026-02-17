package org.firstinspires.ftc.teamcode.Common;

/**
 * Configuration class for alliance-specific parameters.
 * This centralizes all Blue/Red differences to avoid code duplication.
 */
public class AllianceConfig {
    public enum Alliance {
        BLUE, RED
    }

    private final Alliance alliance;

    // TeleOp Configuration
    private final double initialFlywheelSpeed;
    private final double shootingSpindexerPower;
    private final double normalSpindexerPower;

    // Autonomous Configuration
    private final double startX;
    private final double startY;
    private final double startHeading;
    private final int coordinateMultiplier; // -1 for mirrored coordinates

    private AllianceConfig(Alliance alliance, double initialFlywheelSpeed,
                           double shootingSpindexerPower, double normalSpindexerPower,
                           double startX, double startY, double startHeading,
                           int coordinateMultiplier) {
        this.alliance = alliance;
        this.initialFlywheelSpeed = initialFlywheelSpeed;
        this.shootingSpindexerPower = shootingSpindexerPower;
        this.normalSpindexerPower = normalSpindexerPower;
        this.startX = startX;
        this.startY = startY;
        this.startHeading = startHeading;
        this.coordinateMultiplier = coordinateMultiplier;
    }

    // Factory methods
    public static AllianceConfig forBlue() {
        return new AllianceConfig(
            Alliance.BLUE,
            0.8,      // initialFlywheelSpeed
            0.135,    // shootingSpindexerPower
            0.35,     // normalSpindexerPower
            -48,      // startX
            -48,      // startY
            225,      // startHeading (degrees)
            1         // coordinateMultiplier (no mirroring)
        );
    }

    public static AllianceConfig forRed() {
        return new AllianceConfig(
            Alliance.RED,
            0.85,     // initialFlywheelSpeed
            0.125,    // shootingSpindexerPower (original Red value)
            0.5,      // normalSpindexerPower
            -48,      // startX (mirrored)
            48,       // startY (mirrored)
            135,      // startHeading (mirrored angle)
            -1        // coordinateMultiplier (mirror Y coordinates)
        );
    }

    // Getters
    public Alliance getAlliance() {
        return alliance;
    }

    public double getInitialFlywheelSpeed() {
        return initialFlywheelSpeed;
    }

    public double getShootingSpindexerPower() {
        return shootingSpindexerPower;
    }

    public double getNormalSpindexerPower() {
        return normalSpindexerPower;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getStartHeading() {
        return startHeading;
    }

    public int getCoordinateMultiplier() {
        return coordinateMultiplier;
    }

    public boolean isBlue() {
        return alliance == Alliance.BLUE;
    }

    public boolean isRed() {
        return alliance == Alliance.RED;
    }

    /**
     * Mirrors Y coordinate based on alliance
     */
    public double mirrorY(double y) {
        return y * coordinateMultiplier;
    }

    /**
     * Mirrors angle based on alliance (converts 225° Blue to 135° Red, etc.)
     */
    public double mirrorAngle(double angleDegrees) {
        if (isRed()) {
            // Mirror across vertical axis: 360 - angle, then adjust for symmetry
            return 360 - angleDegrees;
        }
        return angleDegrees;
    }
}
