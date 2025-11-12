# Permission Manager Initialization Fix

## Problem

The app was crashing on startup with the following error:

```
kotlin.UninitializedPropertyAccessException: lateinit property permissionManager has not been initialized
	at com.eic.healthconnectdemo.MainActivity.getPermissionManager(MainActivity.kt:34)
	at com.eic.healthconnectdemo.MainActivity.<init>(MainActivity.kt:38)
```

## Root Cause

The crash occurred due to **incorrect initialization order** in `MainActivity`. The issue was:

1. **Line 34**: `permissionManager` declared as `lateinit` with `@Inject` annotation
2. **Line 38**: `registerForActivityResult()` called during **field initialization** (class construction)
3. **Problem**: Hilt dependency injection happens **after** the constructor completes, but field initializers run **during** construction

### Initialization Order in Android Activities

```
1. Constructor starts
2. Field initializers execute (including registerForActivityResult)
3. Constructor completes
4. Hilt injects @Inject fields ← permissionManager injected HERE
5. onCreate() called
```

Since `permissionManager.createPermissionContract()` was called at step 2, but injection happens at step 4, the property was uninitialized when accessed.

## Solution

**Move `registerForActivityResult` initialization to `onCreate()`** after Hilt injection completes.

### Changes Made

**File:** `app/src/main/java/com/eic/healthconnectdemo/MainActivity.kt`

**Before:**
```kotlin
@Inject
lateinit var permissionManager: PermissionManager

private val requestHealthConnectPermissions =
    registerForActivityResult(
        permissionManager.createPermissionContract(),  // ❌ Accessed before injection
    ) { granted ->
        val allGranted = granted.containsAll(PermissionManager.REQUIRED_PERMISSIONS)
        viewModel.setPermissionGranted(allGranted)
        updatePermissionStatus(allGranted)
    }

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
```

**After:**
```kotlin
@Inject
lateinit var permissionManager: PermissionManager

private lateinit var requestHealthConnectPermissions: ActivityResultLauncher<Set<String>>

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Initialize permission launcher after Hilt injection ✅
    requestHealthConnectPermissions =
        registerForActivityResult(
            permissionManager.createPermissionContract(),
        ) { granted ->
            val allGranted = granted.containsAll(PermissionManager.REQUIRED_PERMISSIONS)
            viewModel.setPermissionGranted(allGranted)
            updatePermissionStatus(allGranted)
        }
```

### Key Changes

1. **Changed property declaration** from `val` to `lateinit var`
2. **Added explicit type**: `ActivityResultLauncher<Set<String>>`
3. **Moved initialization** to `onCreate()` after `setContentView()`
4. **Timing**: Now initializes after Hilt injection completes

## Why This Works

### Activity Lifecycle with Hilt

```
MainActivity constructor
    ↓
Field initializers execute
    ↓
Constructor completes
    ↓
Hilt.inject(this) ← permissionManager injected
    ↓
onCreate() called
    ↓
registerForActivityResult() ← Safe to use permissionManager
```

By moving the initialization to `onCreate()`, we ensure `permissionManager` is already injected and ready to use.

## Best Practices for registerForActivityResult

### ✅ Correct Patterns

**Pattern 1: No dependencies**
```kotlin
private val launcher = registerForActivityResult(SomeContract()) { result ->
    // Handle result
}
```

**Pattern 2: With injected dependencies**
```kotlin
@Inject lateinit var dependency: SomeDependency

private lateinit var launcher: ActivityResultLauncher<Input>

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    launcher = registerForActivityResult(dependency.createContract()) { result ->
        // Handle result
    }
}
```

### ❌ Incorrect Patterns

**Don't access injected fields during initialization:**
```kotlin
@Inject lateinit var dependency: SomeDependency

// ❌ WRONG - dependency not injected yet
private val launcher = registerForActivityResult(dependency.createContract()) { }
```

## Verification

### Build Status
✅ Build successful: `./gradlew assembleQaDebug` (7s)
✅ Code style: `./gradlew ktlintCheck` (3s)

### Testing Checklist

- [x] App launches without crash
- [ ] Permission request dialog appears when clicking "Check Permissions"
- [ ] Permissions can be granted/denied successfully
- [ ] Permission status updates correctly in UI
- [ ] Temperature recording works after permissions granted
- [ ] App handles permission denial gracefully

## Related Files

- `MainActivity.kt` - Fixed initialization order
- `PermissionManager.kt` - Provides permission contract
- `TemperatureViewModel.kt` - Handles permission state

## Prevention

To prevent similar issues:

1. **Never access `@Inject` fields in field initializers** - They're not injected yet
2. **Use `onCreate()` for initialization** that depends on injected dependencies
3. **Prefer `lateinit var`** over `val` when initialization must be deferred
4. **Add explicit types** for `lateinit` properties to catch errors early
5. **Test on real devices** - These errors may not appear in unit tests

## Additional Notes

### Why registerForActivityResult Must Be Called Early

According to Android documentation, `registerForActivityResult()` must be called **before** the activity is `STARTED`. This is why we call it in `onCreate()` rather than later lifecycle methods like `onStart()` or `onResume()`.

### Hilt Injection Timing

Hilt injects dependencies:
- **Activities**: Between constructor and `onCreate()`
- **Fragments**: Between constructor and `onAttach()`
- **ViewModels**: During construction (safe to use in init blocks)

## References

- [Android Activity Result API](https://developer.android.com/training/basics/intents/result)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [Kotlin lateinit Properties](https://kotlinlang.org/docs/properties.html#late-initialized-properties-and-variables)
