# Turret Servo Conversion Guide

> **⚠️ ACTION REQUIRED: Review and decide whether to delete this file**
>
> **Status:** UNTRACKED - Never committed by anyone (created Jan 31, 2025)
>
> **What this is:** Documentation for converting turret from motor to CRServo + external encoder
>
> **Current reality:** Robot already uses standard Servo for turret (TurretHardware.java uses `Servo turret`)
> - Current turret: Standard positional servo (0-1 range, built-in position control)
> - This guide: CRServo approach (continuous rotation + external encoder for feedback)
>
> **Why this might exist:** Exploratory work from Jan 31 when team was solving motor count issues
>
> **Can be deleted if:**
> - Current servo approach (TurretHardware.java) is working well
> - Team is happy with positional servo performance
> - No need for alternative CRServo implementation
>
> **Keep if:**
> - Team wants to explore CRServo approach for better control
> - Current servo has issues (limited rotation, position drift, etc.)
> - Want to preserve this research for future reference
>
> ---

## Problem
FTC has an 8-motor limit, but the robot currently uses 9 motors including the turret.

## Solution
Convert turret from motor to CRServo + external encoder

---

## Hardware Requirements

### Required:
1. **CRServo** - For turret actuation (replaces motor)
   - Examples: REV Smart Robot Servo, goBILDA Dual Mode Servo
   - Must support continuous rotation

2. **External Encoder** - For position feedback (one of these):

**Option A: REV Through Bore Encoder (Recommended)**
- Part #: REV-11-1271
- 8192 CPR (counts per revolution)
- Absolute encoder (doesn't lose position on power cycle!)
- Mount directly on turret shaft after pulley reduction
- Cost: ~$60

**Option B: Use Spare Motor Port as Encoder Input (Free)**
- If you have a spare encoder from drivetrain or other mechanism
- Plug into unused motor port in Control Hub
- Code reads it as if it's a motor encoder
- Examples: Spare mecanum wheel encoder, extra dead wheel encoder

**Option C: Dead Wheel Encoder**
- Compliant wheel pressed against turret
- Can slip, less accurate
- Backup option if can't mount encoder on shaft

---

## Encoder Calculation

### If using REV Through Bore Encoder (mounted on turret shaft):
```
8192 ticks per revolution ÷ 360 degrees = 22.76 ticks per degree
```

### If using motor encoder (mounted on motor shaft before pulley):
```
Motor: GoBILDA 435 RPM = 495.6 ticks/rev (28 CPR × 17.7 gear ratio)
Pulley ratio: 110/48 = 2.2917
Ticks per turret degree = 495.6 × 2.2917 / 360 = 3.155 ticks per degree
```

Update `TICKS_PER_DEGREE` in the code based on your setup!

---

## Code Files Created

1. **TurretV2Servo.java** - Servo version of TurretHardware
   - Uses CRServo for actuation
   - Uses OverflowEncoder for position feedback
   - Provides same interface as motor version

2. **TurretTestServo.java** - Test OpMode for servo turret
   - Full PD control
   - Angle limiting
   - Warning zones
   - Same features as motor version

---

## Implementation Steps

### Step 1: Hardware Configuration

In Robot Configuration on Driver Station:

**Remove:**
- "turret" as DcMotor

**Add:**
- "turret" as CRServo (Continuous Rotation Servo)
- "turretEncoder" or "rightBack" as DcMotor (for encoder input)
  - Note: You're not using this as a motor, just reading its encoder!

### Step 2: Physical Installation

**Mount Encoder:**

**Option A (REV Through Bore):**
1. Mount encoder on turret shaft after pulley reduction
2. Ensure it rotates with turret, not with motor
3. Connect encoder cable to motor port on Control Hub

**Option B (Spare Motor Port):**
1. Identify unused motor port (e.g., "rightBack" if 3-wheel drive)
2. Connect your spare encoder to this port
3. Mount encoder to track turret rotation

### Step 3: Update Code

**Replace TurretHardware with TurretV2Servo:**

In your TeleOp files or wherever TurretHardware is used:
```java
// OLD:
// import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretHardware;
// TurretHardware turret = new TurretHardware(hardwareMap);

// NEW:
import org.firstinspires.ftc.teamcode.ProgrammingBoards.TurretV2Servo;
TurretV2Servo turret = new TurretV2Servo(hardwareMap);
```

**Update TurretV2Servo.java:**
1. Line 50: Choose your encoder option (A or B)
2. Lines 60-61: Comment/uncomment based on your encoder choice
3. Lines 36-47: Update TICKS_PER_DEGREE for your encoder

### Step 4: Calibration

1. **Set Encoder Type** in TurretV2Servo.java:
   - For Through Bore on turret shaft: Use OPTION 1 (22.76 ticks/degree)
   - For motor encoder on motor shaft: Use OPTION 2 (3.155 ticks/degree)

2. **Test Encoder Direction:**
   - Manually rotate turret clockwise
   - Check if encoder increases
   - If backwards, reverse encoder wires or negate the value in code

3. **Verify Angle Limits:**
   - Manually position turret at center
   - Note encoder reading
   - Rotate to left limit, note angle
   - Rotate to right limit, note angle
   - Update MIN_ANGLE and MAX_ANGLE in TurretTestServo.java

### Step 5: Testing

Run **TurretTestServo** OpMode:

1. ✅ Verify encoder reads correctly
2. ✅ Check angle calculations are accurate
3. ✅ Test angle limits work
4. ✅ Verify servo responds to Limelight tracking
5. ✅ Confirm no overshoot
6. ✅ Check cables don't get tangled at limits

---

## Key Differences from Motor Version

### What Works the Same:
✅ Angle limiting
✅ PD control
✅ Limelight vision tracking
✅ Velocity-based damping
✅ All safety features

### What's Different:
- Uses `turret.setPower()` instead of `turret.turret.setPower()`
- Uses `turret.getCurrentPosition()` from external encoder
- Uses `turret.getVelocity()` from external encoder
- May need to track encoder offset if using incremental encoder

### What You Gain:
✅ **Saves 1 motor** - Now within 8-motor limit!
✅ **Same performance** - All features preserved
✅ **Potentially lighter** - Servos often lighter than motors
✅ **Lower power** - Servos draw less current

### What You Lose:
❌ Slightly more wiring complexity (separate encoder)
❌ One more point of potential failure (encoder connection)

---

## Troubleshooting

**Encoder reads zero or doesn't change:**
- Check encoder is plugged into correct motor port
- Verify motor port is configured in Robot Configuration
- Check encoder wires aren't loose
- Try reading encoder directly: `hardwareMap.get(DcMotorEx.class, "turretEncoder").getCurrentPosition()`

**Angle limits not working:**
- Verify TICKS_PER_DEGREE is correct for your encoder
- Check encoder direction (may need to negate)
- Confirm MIN_ANGLE/MAX_ANGLE are set correctly
- Test encoder manually with telemetry

**Turret doesn't move:**
- Verify "turret" is configured as CRServo (not regular Servo!)
- Check servo is plugged in correctly
- Try setting power directly: `turretServo.setPower(0.5)`
- Ensure servo power wire is connected

**Position drifts over time:**
- Use absolute encoder (REV Through Bore) instead of incremental
- Or implement position reset routine at known position
- Check for mechanical slipping

---

## Summary

This conversion lets you keep all your turret functionality (angle limiting, vision tracking, PD control) while saving a motor for the 8-motor limit. The external encoder provides the same position feedback the motor encoder did, so your control algorithms work identically.

The files **TurretV2Servo.java** and **TurretTestServo.java** are ready to use once you:
1. Install your external encoder
2. Update the encoder configuration in code
3. Test and calibrate

Good luck with the conversion!
