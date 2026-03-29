# Sloth Fast Deployment Setup

## What is Sloth?

Sloth is a hot code reload library for FTC that deploys code to your Control Hub in **under 1 second** instead of the typical **40+ seconds** for a full APK rebuild and install.

**From:** Dairy Foundation (https://github.com/Dairy-Foundation/Sloth)
**Version:** 0.2.4

---

## How It Works

**Normal Deployment:**
1. Android Studio builds entire APK (~30 seconds)
2. Transfers APK to Control Hub (~5-10 seconds)
3. Installs APK (~5 seconds)
**Total: 40+ seconds**

**Sloth Deployment:**
1. Gradle builds only changed classes
2. Hot-swaps changed code on Control Hub
**Total: <1 second** ⚡

---

## Setup Instructions (Already Done!)

The following changes have been made to enable Sloth:

### ✅ Root `build.gradle`
- Added Dairy Foundation repository to buildscript
- Added Sloth Load plugin (v0.2.4)

### ✅ `TeamCode/build.gradle`
- Applied Sloth plugin
- Added Dairy Foundation repositories (releases + snapshots)
- Added Sloth library dependency (v0.2.4)

---

## How to Use Sloth

### Step 1: Initial Full Deploy (First Time Only)

Before using Sloth, deploy your code normally once:

1. **Android Studio:** Run → Run 'TeamCode'
2. Wait for full APK installation (~40 seconds)
3. Verify robot works

This creates the base APK that Sloth will hot-reload into.

---

### Step 2: Configure Sloth Gradle Tasks in Android Studio

You need to create two run configurations:

#### **Task 1: removeSlothRemote**

1. Click **Run → Edit Configurations**
2. Click **+ (Add)** → **Gradle**
3. Configure:
   - **Name:** `removeSlothRemote`
   - **Gradle project:** Select your project
   - **Tasks:** `removeSlothRemote`
   - **Arguments:** (leave empty)
4. Click **OK**

**Purpose:** Cleans Sloth's remote cache before deployment

---

#### **Task 2: deploySloth**

1. Click **Run → Edit Configurations**
2. Click **+ (Add)** → **Gradle**
3. Configure:
   - **Name:** `deploySloth`
   - **Gradle project:** Select your project
   - **Tasks:** `deploySloth`
   - **Arguments:** (leave empty)
4. Click **OK**

**Purpose:** Hot-deploys your code changes in <1 second

---

### Step 3: Fast Deploy Workflow

**Every time you make code changes:**

1. **Edit your code** (e.g., change motor power, add telemetry)
2. **Select** `deploySloth` from run configurations dropdown
3. **Click Run** (▶️ green play button)
4. **Wait ~1 second**
5. **Test on robot!**

No need to restart the OpMode - changes take effect immediately!

---

## When to Use Each Method

### Use Sloth (`deploySloth`) For:
- ✅ Quick iterative testing
- ✅ Tuning PID values
- ✅ Adjusting motor speeds
- ✅ Changing autonomous paths
- ✅ Adding/modifying telemetry
- ✅ Most code changes

### Use Full Deploy For:
- ⚠️ First deployment after clean build
- ⚠️ Adding new hardware devices
- ⚠️ Changing gradle dependencies
- ⚠️ Major structural changes
- ⚠️ When Sloth deployment fails

---

## Troubleshooting

### Issue: "Task 'deploySloth' not found"

**Solution:**
1. Make sure you're on the correct branch with Sloth setup
2. In Android Studio: **File → Sync Project with Gradle Files**
3. Try again

---

### Issue: Sloth deployment fails

**Solution:**
1. Run `removeSlothRemote` task first
2. Then run `deploySloth` again
3. If still failing, do a full APK deploy once

---

### Issue: Changes not appearing on robot

**Solution:**
1. Make sure Control Hub is connected via USB or WiFi
2. Restart the OpMode on Driver Station
3. If persistent, do a full APK deploy

---

### Issue: "ClassNotFoundException" after Sloth deploy

**Solution:**
This means Sloth can't hot-reload this type of change. Do a full deploy:
1. Stop using Sloth for this change
2. Run normal deployment: Run → Run 'TeamCode'
3. After full deploy completes, you can resume using Sloth

---

## Pro Tips

### Tip 1: Clean Before Competition
Before competitions, run `removeSlothRemote` then do a full APK deploy to ensure clean state.

### Tip 2: Iterative Tuning
Sloth is perfect for tuning:
- Use `deploySloth` to test different PID values
- No waiting between iterations
- Converge on optimal values much faster

### Tip 3: Use with FTC Dashboard
Combine Sloth with FTC Dashboard for real-time tuning:
1. Deploy changes with Sloth (<1 second)
2. See results in Dashboard graphs immediately
3. Iterate quickly

### Tip 4: Keyboard Shortcut
Set a keyboard shortcut for `deploySloth`:
1. **File → Settings → Keymap**
2. Search for "deploySloth"
3. Right-click → Add Keyboard Shortcut
4. Recommended: `Ctrl+Shift+D` (Windows/Linux) or `Cmd+Shift+D` (Mac)

---

## Additional Resources

- **Sloth GitHub:** https://github.com/Dairy-Foundation/Sloth
- **Dairy Foundation Docs:** https://dairy.foundation/
- **FTC Dashboard:** Already installed (combine with Sloth for best workflow)

---

## Version Info

- **Sloth Version:** 0.2.4
- **Load Plugin Version:** 0.2.4
- **Added:** February 19, 2026
- **Branch:** add_sloth_fast_deployment

---

**Happy Fast Deploying! 🚀**
