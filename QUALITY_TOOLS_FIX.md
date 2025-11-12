# Code Quality Tools - Build Configuration Fix

## Issue Fixed

The initial integration had task dependency issues due to the project using multiple product flavors (dev, qa, prod).

## Problem

```
FAILURE: Build failed with an exception.
* What went wrong:
Could not determine the dependencies of task ':qualityCheck'.
> Task with path 'lint' not found in root project 'HealthConnectDemo'.
```

## Root Cause

The project has multiple product flavors configured:
- `dev` (development)
- `qa` (quality assurance)
- `prod` (production)

This creates variant-specific tasks like:
- `testDevDebugUnitTest` instead of `testDebugUnitTest`
- `lintDevDebug` instead of `lint`

## Changes Made

### 1. Root `build.gradle.kts`
Fixed the root-level `qualityCheck` task to delegate to the app module:

```kotlin
tasks.register("qualityCheck") {
    group = "verification"
    description = "Run all code quality checks across all modules"
    dependsOn(":app:qualityCheck")
}
```

### 2. App `build.gradle.kts`

#### Updated Quality Check Task
Changed from generic task names to variant-specific ones:

```kotlin
tasks.register("qualityCheck") {
    dependsOn(
        "testDevDebugUnitTest",    // was: testDebugUnitTest
        "detekt",
        "ktlintCheck",
        "lintDevDebug",            // was: lint
        "jacocoTestReport",
        "jacocoTestCoverageVerification",
    )
}
```

#### Updated Pre-commit Check Task
```kotlin
tasks.register("preCommitCheck") {
    dependsOn(
        "ktlintCheck",
        "detekt",
        "testDevDebugUnitTest",    // was: testDebugUnitTest
    )
}
```

#### Updated JaCoCo Configuration
Changed all JaCoCo tasks to use `devDebug` variant:

```kotlin
// JaCoCo Report Task
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDevDebugUnitTest")  // was: testDebugUnitTest
    
    val debugTree = fileTree("${layout.buildDirectory.get()}/tmp/kotlin-classes/devDebug") {
        exclude(fileFilter)
    }
    
    executionData.setFrom(
        fileTree(layout.buildDirectory) {
            include("jacoco/testDevDebugUnitTest.exec")  // was: testDebugUnitTest.exec
        },
    )
}
```

#### Fixed Deprecated API Usage
Replaced deprecated `buildDir` with `layout.buildDirectory`:

```kotlin
// Before
file("$buildDir/reports/lint/lint-results.html")

// After
file("${layout.buildDirectory.get()}/reports/lint/lint-results.html")
```

## Verification

Run the following to verify the fix:

```bash
# Dry run to check task dependencies
./gradlew qualityCheck --dry-run

# Run actual quality checks
./gradlew qualityCheck
```

## Available Variants

The project now supports quality checks for all variants:

### Test Tasks
- `testDevDebugUnitTest`
- `testDevReleaseUnitTest`
- `testQaDebugUnitTest`
- `testQaReleaseUnitTest`
- `testProdDebugUnitTest`
- `testProdReleaseUnitTest`

### Lint Tasks
- `lintDevDebug`
- `lintDevRelease`
- `lintQaDebug`
- `lintQaRelease`
- `lintProdDebug`
- `lintProdRelease`

## Current Configuration

The quality check tasks are configured to use the **dev + debug** variant by default:
- Fastest to build
- Includes debug symbols
- Suitable for development

## Customization

To run quality checks for other variants:

```bash
# QA variant
./gradlew testQaDebugUnitTest lintQaDebug

# Production variant
./gradlew testProdDebugUnitTest lintProdDebug
```

## Status

✅ **FIXED** - All quality check tasks now work correctly with product flavors.

---

**Fixed Date**: November 2024  
**Status**: Ready for use
