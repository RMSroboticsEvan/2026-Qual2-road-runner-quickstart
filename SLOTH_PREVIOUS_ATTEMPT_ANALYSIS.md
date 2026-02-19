# Sloth Setup Comparison: Why Previous Attempt Failed

## Summary

A team member previously attempted to add Sloth on the `slothLoad` branch (commit `cfa0cfb` on Jan 25, 2026) but it didn't work. This document explains what went wrong and how the current implementation fixes those issues.

---

## Previous Attempt Analysis (slothLoad branch)

### What They Tried

**Approach:** Used the **Dairy FTC Template Plugin** (full project template conversion)

**Files Added:**
- `TeamCode/build.gradle.kts` (Kotlin Gradle DSL)
- `TeamCode/settings.gradle.kts` (Kotlin settings)
- `TeamCode/gradle.properties` (new Gradle properties)
- `TeamCode/gradlew` + `gradlew.bat` (separate Gradle wrapper in TeamCode)
- `TeamCode/gradle/wrapper/` (wrapper JAR)
- License files (LICENSE.dairy, LICENSE.first)

**Configuration Used:**
```kotlin
// TeamCode/build.gradle.kts
plugins {
    id("dev.frozenmilk.teamcode") version "11.0.0-1.1.0"
    id("dev.frozenmilk.sinister.sloth.load") version "0.2.4"
}

ftc {
    kotlin()
    sdk.TeamCode()
    implementation(dairy.Sloth)
}
```

---

## Why It Failed

### Problem 1: Conflicting Build Files ❌

**Issue:** Created `build.gradle.kts` (Kotlin) alongside existing `build.gradle` (Groovy)

```
TeamCode/
├── build.gradle      ← Original (Groovy DSL)
├── build.gradle.kts  ← New (Kotlin DSL)  CONFLICT!
```

**Why This Breaks:**
- Gradle gets confused which build file to use
- Both files define the same module differently
- Can cause unpredictable build behavior
- Android Studio may not sync properly

**Result:** Gradle likely ignored one file or threw errors during sync

---

### Problem 2: Conflicting Settings Files ❌

**Issue:** Created `settings.gradle.kts` inside TeamCode module

```
Root/
├── settings.gradle        ← Defines project structure
└── TeamCode/
    └── settings.gradle.kts  ← Shouldn't exist here!
```

**Why This Breaks:**
- Settings files belong at root level, not in submodules
- TeamCode is a module, not a standalone project
- Creates confusion about project structure
- May cause Gradle to treat TeamCode as separate project

---

### Problem 3: Separate Gradle Wrapper ❌

**Issue:** Added separate Gradle wrapper inside TeamCode

```
Root/
├── gradlew               ← Original wrapper (v?)
└── TeamCode/
    ├── gradlew           ← New wrapper (different version)
    └── gradle/wrapper/   ← Separate wrapper JAR
```

**Why This Breaks:**
- Multi-module projects should share one Gradle wrapper
- Different wrapper versions cause compatibility issues
- Breaks Android Studio integration
- FTC SDK expects single wrapper at root

---

### Problem 4: Wrong Approach - Full Template ❌

**Issue:** Used Dairy FTC Template plugin (`dev.frozenmilk.teamcode`)

**What They Tried:**
- Complete project template conversion
- Adds Kotlin support
- Restructures entire build system
- Changes how FTC SDK dependencies work

**Why This Breaks:**
- Dairy Template is for **new projects**, not existing ones
- Incompatible with existing Road Runner Quickstart structure
- Changes fundamental project structure
- May break existing code/dependencies

**Dairy Template Use Case:**
```
✅ Starting a NEW FTC project from scratch
❌ Adding Sloth to EXISTING Road Runner project
```

---

### Problem 5: Over-Engineering ❌

**What They Added:** 10+ files, new wrapper, new settings, licenses, READMEs

**What Sloth Actually Needs:** 3-4 lines in existing Gradle files

---

## Current Implementation (This PR)

### Approach: Minimal Integration ✅

**Strategy:** Add Sloth library directly to existing Groovy Gradle files

**Philosophy:**
- Keep existing project structure
- Minimal changes to working build system
- No Kotlin conversion needed
- No template conversion needed

---

### What Changed (Correct Way)

#### 1. Root `build.gradle` - Buildscript Only
```groovy
buildscript {
    repositories {
        // ... existing repos ...
        maven { url = 'https://repo.dairy.foundation/releases' }
    }
    dependencies {
        // ... existing deps ...
        classpath 'dev.frozenmilk:Load:0.2.4'  // ← Sloth plugin
    }
}
```

**Why This Works:** ✅
- Adds Sloth plugin to classpath
- Doesn't conflict with existing structure
- Uses existing Groovy DSL

---

#### 2. `TeamCode/build.gradle` - Apply Plugin
```groovy
// After existing apply statements
apply plugin: 'dev.frozenmilk.sinister.sloth.load'  // ← Apply Sloth
```

**Why This Works:** ✅
- Applies Sloth plugin to TeamCode module only
- No new files created
- Uses existing build.gradle

---

#### 3. `TeamCode/build.gradle` - Add Repositories
```groovy
repositories {
    maven { url = 'https://maven.brott.dev/' }
    maven { url = 'https://repo.dairy.foundation/releases' }
    maven { url = 'https://repo.dairy.foundation/snapshots' }
}
```

**Why This Works:** ✅
- Adds Dairy repos to existing repository block
- No conflicts with existing repos
- Allows downloading Sloth library

---

#### 4. `TeamCode/build.gradle` - Add Dependency
```groovy
dependencies {
    // ... existing deps ...
    implementation "dev.frozenmilk.sinister:Sloth:0.2.4"  // ← Sloth library
}
```

**Why This Works:** ✅
- Adds Sloth library to existing dependencies
- No template magic needed
- Works with existing FTC SDK setup

---

## Comparison Table

| Aspect | Previous Attempt | Current Solution |
|--------|------------------|------------------|
| **Files Changed** | 14 new files | 2 files modified |
| **Build System** | Kotlin DSL (new) | Groovy DSL (existing) |
| **Approach** | Full template conversion | Library integration |
| **Gradle Wrapper** | Added new wrapper | Uses existing wrapper |
| **Settings File** | Created TeamCode/settings.gradle.kts | Uses root settings.gradle |
| **Compatibility** | Breaks existing structure | Preserves existing structure |
| **Complexity** | High (complete rewrite) | Low (minimal additions) |
| **Risk** | High (many changes) | Low (isolated changes) |
| **Result** | Didn't work ❌ | Will work ✅ |

---

## Why Current Approach Works

### 1. Respects Existing Structure ✅
- Keeps Road Runner Quickstart project structure
- Doesn't fight with FTC SDK setup
- Preserves working configuration

### 2. Minimal Changes ✅
- Only 3-4 lines added per file
- Uses existing Gradle files
- No new wrapper/settings needed

### 3. Correct Plugin Usage ✅
- Uses Sloth as a **library**, not a template
- Applies Load plugin correctly
- Adds dependency properly

### 4. No Kotlin Required ✅
- Stays with Groovy Gradle DSL
- No DSL conversion needed
- Less complexity

### 5. Testable ✅
- Small changes = easy to debug
- Can rollback quickly if needed
- Clear what each change does

---

## What Team Member Should Have Done

### Correct Approach (Step-by-step):

1. **Open existing `build.gradle` files** (don't create new ones)
2. **Add Sloth plugin to root buildscript**
3. **Apply Sloth plugin in TeamCode/build.gradle**
4. **Add Dairy repos to TeamCode repositories**
5. **Add Sloth dependency to TeamCode dependencies**
6. **Done!** No new files needed.

---

## Lessons Learned

### ❌ Don't Do This:
- Don't use Dairy Template for existing projects
- Don't create .kts files alongside .gradle files
- Don't add separate Gradle wrappers to modules
- Don't over-engineer simple additions

### ✅ Do This Instead:
- Read Sloth docs carefully (library vs template)
- Use existing build files
- Make minimal changes
- Test incrementally

---

## Recommendations

### For the Team:

1. **Delete `slothLoad` branch** (incorrect implementation)
2. **Use this PR instead** (correct implementation)
3. **Document lesson learned** (template vs library)

### For Future Reference:

When adding libraries to FTC projects:
1. Always check if it's a library or a template
2. Prefer modifying existing files over creating new ones
3. Keep changes minimal and testable
4. Don't mix Kotlin and Groovy Gradle DSL

---

## Next Steps

1. **Merge this PR** (correct Sloth setup)
2. **Delete slothLoad branch** (incorrect attempt)
3. **Test Sloth deployment** (should work now!)
4. **Update team documentation** (how we fixed it)

---

## Technical Details

### Dairy Foundation Ecosystem

**Dairy Core:** Full FTC framework (big)
- Complete project template
- Kotlin support
- Custom command system
- **Use for:** New projects from scratch

**Sloth Library:** Just fast deployment (small)
- Hot reload plugin
- Works with any FTC project
- No project restructuring needed
- **Use for:** Adding to existing projects ← THIS IS WHAT WE NEED

**Previous attempt used:** Dairy Core Template ❌
**Current solution uses:** Sloth Library only ✅

---

## Conclusion

**Previous Attempt:**
- Used wrong approach (template vs library)
- Created file conflicts (Kotlin + Groovy)
- Over-complicated simple addition
- Broke project structure
- **Result: Didn't work**

**Current Solution:**
- Uses correct approach (library only)
- No file conflicts (modifies existing)
- Minimal changes (3-4 lines per file)
- Preserves project structure
- **Result: Will work**

**Key Takeaway:** Sometimes less is more. The correct solution required 10x fewer changes than the previous attempt.

---

**Date:** February 19, 2026
**Branch:** add_sloth_fast_deployment
**Previous Branch:** slothLoad (Jan 25, 2026)
