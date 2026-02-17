# Turret Auto-Align System Documentation

## Overview

The new `TurretController` provides a unified turret aiming system that can be used in both TeleOp and Autonomous. It supports multiple aiming modes that can be switched on-the-fly, including odometry-based aiming with AprilTag correction.

## Problem Solved

**Before:**
- TeleOp had NO turret control (completely disabled)
- Autonomous used only hardcoded angles (vulnerable to being bumped)
- Odometry drift would cause misses without ability to correct
- No way to switch between aiming methods during match

**After:**
- TeleOp has full turret control with 4 aiming modes
- Autonomous can optionally use auto-align to recover from bumps
- Hybrid mode uses odometry but corrects with AprilTag when available
- Driver can cycle through modes and reset odometry mid-match

## Aiming Modes

### 1. MANUAL Mode
- **Use:** Driver controls turret directly with D-pad
- **Controls:**
  - D-pad left/right (gamepad2): Adjust turret angle
- **When to use:** When automated systems fail, or for fine manual adjustment

### 2. ODOMETRY Mode
- **Use:** Auto-aim using drivetrain position (odometry)
- **Pros:** Fast, always available
- **Cons:** Drifts over time (10-20° after heavy movement)
- **When to use:** Early in match when odometry is accurate

### 3. APRILTAG Mode
- **Use:** Auto-aim using Limelight AprilTag detection only
- **Pros:** Always accurate when AprilTag visible
- **Cons:** Requires line of sight to AprilTag, slower to react
- **When to use:** When odometry has drifted significantly

### 4. HYBRID Mode (RECOMMENDED)
- **Use:** Odometry-based aiming with AprilTag correction
- **How it works:**
  1. Uses odometry to calculate initial aim (fast)
  2. When AprilTag visible, measures error and stores correction offset
  3. If error > 5°, switches to pure AprilTag mode
  4. Otherwise uses odometry + correction offset
- **Pros:** Best of both worlds - fast and accurate
- **Cons:** Slightly more complex, requires AprilTag working properly
- **When to use:** When AprilTag is reliable and you want drift correction

**Note:** TeleOp starts in ODOMETRY mode by default. Press Triangle to cycle to HYBRID mode.

## TeleOp Controls

### Turret Controls (Built into BaseTeleOp)

| Button | Action |
|--------|--------|
| **Triangle (gamepad1)** | Cycle aiming mode (MANUAL → ODOMETRY → HYBRID → APRILTAG → MANUAL) |
| **Cross (gamepad1)** | Reset odometry using AprilTag (use at reset corner: 59, -59) |
| **D-pad left/right (gamepad2)** | Manual turret control (only works in MANUAL mode) |

### Telemetry Display

Turret status shows on driver station:
```
Turret: Mode: HYBRID | AT Visible: true | Odo Angle: 45.2° | AT Error: 1.3° | Correction: -2.1°
```

## Autonomous Usage

### Option 1: Hardcoded Angles (Default)
```java
// In your AutoBlueTop or AutoRedTop - do nothing!
// By default, useAutoAlign = false, so hardcoded angles are used
```

### Option 2: Enable Auto-Align (Resilient to Bumps)
```java
@Autonomous(name = "Auto Blue Top V2 (Auto-Align)")
public class AutoBlueTopAutoAlign extends BaseAutonomousTop {
    @Override
    protected AllianceConfig getAllianceConfig() {
        useAutoAlign = true;  // Enable auto-align!
        return AllianceConfig.forBlue();
    }
}
```

With `useAutoAlign = true`:
- Turret uses HYBRID mode during autonomous
- If robot gets bumped, turret corrects using odometry + AprilTag
- Falls back to hardcoded angles if AprilTag not visible
- Adds 200ms aiming delay before each shot

## Code Structure

### Files Added/Modified

**New:**
- `Common/TurretController.java` - Unified turret control with 4 modes

**Modified:**
- `Common/BaseTeleOp.java` - Integrated turret control with driver controls
- `Common/BaseAutonomousTop.java` - Optional auto-align support

**Unchanged:**
- `ProgrammingBoards/TurretHardware.java` - Low-level turret hardware interface
- TeleOpBlue.java / TeleOpRed.java - No changes needed (inherits from BaseTeleOp)
- AutoBlueTop.java / AutoRedTop.java - No changes needed (inherits from BaseAutonomousTop)

### Architecture

```
TurretController (Common/)
    ├── Manages aiming modes
    ├── Coordinates odometry + AprilTag
    └── Provides update() method for main loop

BaseTeleOp (Common/)
    ├── Creates TurretController (ODOMETRY mode by default)
    ├── Handles driver input (mode switching, reset, manual control)
    └── Calls turretController.update() every loop

BaseAutonomousTop (Common/)
    ├── Creates TurretController (MANUAL or HYBRID based on useAutoAlign)
    ├── Provides aimTurret(fallbackAngle) method
    └── Updates odometry and calls turretController.update() when shooting
```

## How Odometry Drift Correction Works

### The Problem
After driving around the field, drivetrain odometry can drift 10-20° from true position. This causes the turret to aim incorrectly even though odometry-based calculation is fast.

### The Solution: HYBRID Mode

1. **Primary aiming:** Calculate angle using odometry (fast, always available)
2. **AprilTag monitoring:** When AprilTag visible, check how far off we are
3. **Store correction:** `aprilTagCorrectionOffset = measured_error`
4. **Apply correction:** `final_angle = odometry_angle - correction_offset`
5. **Fallback:** If AprilTag shows large error (>5°), use pure AprilTag mode

### Reset Corner
When odometry drift gets too large, driver presses Cross button at the "reset corner" (59, -59 field position with known AprilTag):
1. Turret uses AprilTag to get ground truth angle
2. Resets odometry pose to known position (59, -59, 0°)
3. Clears correction offset
4. Odometry is now accurate again

## Testing Recommendations

### TeleOp Testing
1. Start in ODOMETRY mode (default) - pure odometry-based aiming
2. Drive around and shoot - verify turret tracks correctly
3. Press Triangle to cycle to HYBRID mode - verify AprilTag correction works (if AprilTag functional)
4. Press Triangle again to test other modes - verify each works
5. Drive to reset corner (59, -59) - press Cross - verify odometry resets
6. Deliberately drive in circles to cause drift - verify HYBRID mode corrects (when switched to HYBRID)

### Autonomous Testing
1. Test default (useAutoAlign = false) - verify hardcoded angles work
2. Test with useAutoAlign = true - verify auto-align works
3. **Bump test:** Start auto, then physically push robot during pickup phase
   - Turret should auto-correct when it aims for next shot
4. Test with AprilTag blocked - verify falls back to hardcoded angles

## Troubleshooting

### "Turret not moving in TeleOp"
- Check mode - if in MANUAL, must use D-pad to control
- Press Triangle to switch to HYBRID mode

### "Turret aims wrong direction"
- Odometry may have drifted - drive to reset corner and press Cross
- Or press Triangle to switch to APRILTAG mode temporarily

### "Autonomous turret doesn't correct after bump"
- Verify `useAutoAlign = true` in your autonomous class
- Check AprilTag visibility - turret needs to see target to correct

### "AprilTag not detected"
- Verify Limelight is powered and connected
- Check pipeline 0 is configured for AprilTags
- Ensure robot has line of sight to target AprilTag

## Future Enhancements

Possible additions:
1. **Ball counting integration:** Auto-align only when balls loaded
2. **Flywheel speed adjustment:** Change flywheel speed based on calculated distance
3. **Multiple target support:** Switch between different AprilTag targets
4. **Telemetry recording:** Log aiming accuracy for post-match analysis

## Benefits Summary

✅ **Unified system:** Same turret code for TeleOp and Autonomous
✅ **Resilient to bumps:** Autonomous can recover from being pushed
✅ **Drift correction:** Hybrid mode fixes odometry drift automatically
✅ **Driver control:** Can switch modes mid-match based on conditions
✅ **Fallback safety:** Always has hardcoded angles as backup
✅ **Zero code duplication:** All turret logic in one place (TurretController)

---
*Created: February 16, 2026*
*Part of code cleanup branch: paul_2026_0216_code_cleanup*
