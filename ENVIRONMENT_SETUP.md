# Environment Setup Guide

## Quick Start

### 1. Sync Project
After pulling the latest changes, sync your Gradle files:
```bash
./gradlew clean build
```

### 2. Select Build Variant

In Android Studio:
1. Click on **Build Variants** tab (usually on the left side)
2. Select your desired variant:
   - `devDebug` - For development testing
   - `qaDebug` - For QA testing
   - `prodRelease` - For production builds

### 3. Run the App

Click the Run button or use:
```bash
# Run dev build
./gradlew installDevDebug

# Run QA build
./gradlew installQaDebug

# Run production build
./gradlew installProdRelease
```

## Build Variants Explained

### Development (dev)
- **Purpose**: Local development and testing
- **Features**:
  - Logging enabled
  - Debug tools available
  - Can install alongside other variants
  - App name shows "DEV" suffix
  - Package name: `com.eic.healthconnectdemo.dev`

### QA (qa)
- **Purpose**: Quality assurance testing
- **Features**:
  - Logging enabled
  - Connects to QA backend
  - Can install alongside other variants
  - App name shows "QA" suffix
  - Package name: `com.eic.healthconnectdemo.qa`

### Production (prod)
- **Purpose**: Production release
- **Features**:
  - Logging disabled
  - Optimized and minified
  - Connects to production backend
  - Clean app name
  - Package name: `com.eic.healthconnectdemo`

## Environment Configuration

### Accessing Configuration in Code

```kotlin
import com.eic.healthconnectdemo.core.config.AppConfig

// Check current environment
when {
    AppConfig.isDevelopment() -> {
        // Development-specific code
    }
    AppConfig.isQA() -> {
        // QA-specific code
    }
    AppConfig.isProduction() -> {
        // Production-specific code
    }
}

// Get configuration values
val baseUrl = AppConfig.baseUrl
val isLoggingEnabled = AppConfig.isLoggingEnabled
val environment = AppConfig.environment
```

### Using the Logger

```kotlin
import com.eic.healthconnectdemo.core.logger.AppLogger

// Logging (automatically disabled in production)
AppLogger.d("MyTag", "Debug message")
AppLogger.i("MyTag", "Info message")
AppLogger.w("MyTag", "Warning message")
AppLogger.e("MyTag", "Error message", exception)
```

### Using Constants

```kotlin
import com.eic.healthconnectdemo.core.util.Constants

// Access constants
val minTemp = Constants.Temperature.MIN_CELSIUS
val dateFormat = Constants.DateTime.DATE_FORMAT
val playStoreUrl = Constants.HealthConnect.PLAY_STORE_URL
```

### Using Extensions

```kotlin
import com.eic.healthconnectdemo.core.util.*

// Date formatting
val dateString = instant.toDateString()
val timeString = instant.toTimeString()

// Temperature conversion
val fahrenheit = celsius.celsiusToFahrenheit()
val formattedTemp = temperature.formatTemperature("C")

// Toast helpers
context.showToast("Quick message")
context.showLongToast("Longer message")
```

## Building Release APKs

### For QA Testing
```bash
./gradlew assembleQaRelease
```
APK location: `app/build/outputs/apk/qa/release/app-qa-release.apk`

### For Production
```bash
./gradlew assembleProdRelease
```
APK location: `app/build/outputs/apk/prod/release/app-prod-release.apk`

## Installing Multiple Variants

You can install all three variants on the same device simultaneously:

```bash
# Install all debug variants
./gradlew installDevDebug installQaDebug installProdDebug
```

Each variant will appear as a separate app with its own icon and name.

## Troubleshooting

### Build Fails After Switching Variants
1. Clean the project: `Build > Clean Project`
2. Rebuild: `Build > Rebuild Project`
3. Sync Gradle: `File > Sync Project with Gradle Files`

### BuildConfig Not Found
1. Make sure `buildConfig = true` is in `build.gradle.kts`
2. Rebuild the project
3. Invalidate caches: `File > Invalidate Caches / Restart`

### Wrong Environment Running
1. Check Build Variants tab
2. Verify the selected variant matches your intention
3. Uninstall old versions if package name conflicts

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Build

on: [push, pull_request]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Build Dev
        run: ./gradlew assembleDevDebug
      - name: Build QA
        run: ./gradlew assembleQaRelease
      - name: Build Production
        run: ./gradlew assembleProdRelease
```

## Best Practices

1. **Always use the correct variant** for your testing environment
2. **Never commit sensitive data** (API keys, passwords) to version control
3. **Use BuildConfig** for environment-specific values
4. **Test on all variants** before releasing
5. **Keep logging statements** using AppLogger (automatically disabled in prod)
6. **Use constants** instead of hardcoded values
7. **Leverage base classes** for common functionality

## Next Steps

1. Review the [ARCHITECTURE.md](ARCHITECTURE.md) for detailed package structure
2. Check existing code for usage examples
3. Start building features using the modular structure
4. Add your environment-specific configurations as needed
