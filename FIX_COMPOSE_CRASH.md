# Fix for Compose ArrayIndexOutOfBoundsException Crash

## Problem
```
java.lang.ArrayIndexOutOfBoundsException: length=0; index=-5
at androidx.compose.runtime.SlotTableKt.key(SlotTable.kt:3522)
```

This error occurs when there's a mismatch between Kotlin and Compose Compiler versions, and the build cache contains incompatible compiled code.

## Current Configuration ✅
- Kotlin: 1.9.22
- Compose Compiler: 1.5.10 (Compatible!)
- AGP: 8.2.2

## Solution: Clean Rebuild Required

### Option 1: Using Terminal (Recommended)

Run these commands in order:

```bash
# Navigate to project directory
cd /Users/vinod.tak/Documents/Projects/CascadeProjects/HealthConnectDemo

# Stop any running Gradle daemons
./gradlew --stop

# Clean the project (removes all build artifacts)
./gradlew clean

# Delete build directories manually (for thorough cleanup)
rm -rf app/build
rm -rf build
rm -rf .gradle

# Rebuild the project
./gradlew build

# Or if you prefer, just assemble debug
./gradlew assembleDebug
```

### Option 2: Using Android Studio

1. **Stop the app** if it's running
2. **File → Invalidate Caches / Restart...**
3. Select **"Invalidate and Restart"**
4. After restart:
   - **Build → Clean Project**
   - Wait for completion
   - **Build → Rebuild Project**
5. **Run the app** again

### Option 3: Quick Fix (If Options 1 & 2 Don't Work)

```bash
# Complete nuclear option - removes everything
cd /Users/vinod.tak/Documents/Projects/CascadeProjects/HealthConnectDemo

# Stop Gradle
./gradlew --stop

# Remove all build artifacts and caches
rm -rf app/build
rm -rf build
rm -rf .gradle
rm -rf .idea/caches
rm -rf app/.cxx
rm -rf ~/.gradle/caches/

# Sync and rebuild in Android Studio
# File → Sync Project with Gradle Files
# Build → Rebuild Project
```

## Why This Happens

1. You updated Kotlin from 1.9.0 to 1.9.22
2. The Compose Compiler was updated to 1.5.10 (correct!)
3. **BUT** the build cache still contains compiled code from the old incompatible version
4. Gradle doesn't automatically detect this needs a full rebuild
5. The cached incompatible bytecode causes runtime crashes

## Verification Steps

After rebuilding, verify:

1. **Check Gradle sync succeeded**
   - No errors in Build tab
   - All dependencies resolved

2. **Check build output**
   ```
   BUILD SUCCESSFUL in Xs
   ```

3. **Run the app**
   - Should launch without crashes
   - Compose UI should render correctly

4. **Check logcat** (if still crashes)
   - Look for different error messages
   - Share the new stack trace

## Prevention

To avoid this in the future:

1. **Always clean when updating Kotlin/Compose versions**
   ```bash
   ./gradlew clean
   ```

2. **Update both versions together** in the same commit

3. **Use the compatibility matrix** in `KOTLIN_COMPOSE_COMPATIBILITY.md`

## Still Not Working?

If the crash persists after clean rebuild:

### Check 1: Verify Gradle Sync
```bash
./gradlew --refresh-dependencies
```

### Check 2: Check for Multiple Kotlin Versions
Search your project for any hardcoded Kotlin versions:
```bash
grep -r "kotlin.*version" .
```

### Check 3: Verify Compose BOM
The BOM (2024.02.00) should be compatible with Compose Compiler 1.5.10.

### Check 4: Check for Conflicting Dependencies
```bash
./gradlew app:dependencies > dependencies.txt
```
Look for version conflicts in the output.

## Expected Result

After following these steps, the app should:
- ✅ Launch successfully
- ✅ Display the Temperature Input Screen
- ✅ No ArrayIndexOutOfBoundsException
- ✅ Compose UI renders correctly

## Need Help?

If the issue persists:
1. Share the output of `./gradlew clean build`
2. Share any new error messages from logcat
3. Verify the Compose Compiler version in the build output

---

**Last Updated**: November 3, 2025  
**Status**: Configuration is correct, clean rebuild required
