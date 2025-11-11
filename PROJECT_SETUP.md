# Health Connect Demo - Project Setup Guide

## Project Structure

This Android application follows **MVVM + Clean Architecture** principles with the following structure:

```
app/src/main/java/com/eic/healthconnectdemo/
│
├── data/                          # Data Layer
│   ├── mapper/
│   │   └── HealthConnectMapper.kt
│   └── repository/
│       └── HealthConnectRepositoryImpl.kt
│
├── domain/                        # Domain Layer (Business Logic)
│   ├── model/
│   │   ├── Result.kt
│   │   ├── TemperatureRecord.kt
│   │   └── TemperatureUnit.kt
│   ├── repository/
│   │   └── HealthConnectRepository.kt
│   └── usecase/
│       ├── CheckHealthConnectAvailabilityUseCase.kt
│       └── RecordTemperatureUseCase.kt
│
├── presentation/                  # Presentation Layer (UI)
│   ├── state/
│   │   └── TemperatureUiState.kt
│   ├── ui/
│   │   ├── screen/
│   │   │   └── TemperatureInputScreen.kt
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   └── viewmodel/
│       └── TemperatureViewModel.kt
│
├── di/                            # Dependency Injection
│   └── AppModule.kt
│
├── util/                          # Utilities
│   └── Constants.kt
│
├── HealthConnectApp.kt            # Application Class
└── MainActivity.kt                # Main Activity
```

## Setup Instructions

### 1. Prerequisites

- **Android Studio**: Arctic Fox (2020.3.1) or later
- **JDK**: Version 17
- **Minimum SDK**: API 28 (Android 9.0)
- **Target SDK**: API 34 or later

### 2. Open Project in Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the project directory
4. Wait for Gradle sync to complete

### 3. Sync Gradle

The project will automatically sync Gradle dependencies. If not:
- Click **File** → **Sync Project with Gradle Files**

### 4. Install Health Connect

On your test device or emulator:
- For Android 14+: Health Connect is built-in
- For Android 9-13: Install from [Google Play Store](https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata)

### 5. Run the Application

1. Connect your Android device or start an emulator
2. Click the **Run** button (▶️) in Android Studio
3. Select your device
4. The app will build and install

### 6. Grant Permissions

When the app launches:
1. It will request Health Connect permissions
2. Grant **Write Body Temperature** permission
3. You can now record temperature data

## Key Technologies

- **Language**: Kotlin 1.9.0
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Dependency Injection**: Hilt
- **Async**: Kotlin Coroutines + Flow
- **Health API**: Health Connect SDK 1.1.0-alpha07

## Architecture Layers

### Domain Layer (Pure Kotlin)
- **Models**: `TemperatureRecord`, `TemperatureUnit`, `Result`
- **Repository Interface**: `HealthConnectRepository`
- **Use Cases**: Business logic for recording temperature

### Data Layer
- **Repository Implementation**: `HealthConnectRepositoryImpl`
- **Mappers**: Convert domain models to Health Connect SDK models

### Presentation Layer
- **ViewModel**: `TemperatureViewModel` (manages UI state)
- **UI State**: `TemperatureUiState`
- **Composables**: `TemperatureInputScreen`

## Dependencies

All dependencies are managed in `app/build.gradle.kts`:

```kotlin
// Core
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

// Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")

// Health Connect
implementation("androidx.health.connect:connect-client:1.1.0-alpha07")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Hilt
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")
```

## Building the Project

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

### Run Tests
```bash
./gradlew test
```

## Troubleshooting

### Gradle Sync Issues
- Ensure you have a stable internet connection
- Try **File** → **Invalidate Caches / Restart**
- Check that JDK 17 is configured

### Health Connect Not Available
- Ensure Health Connect is installed on the device
- Check that the device is running Android 9.0 or later
- Verify the app has the correct permissions in AndroidManifest.xml

### Permission Issues
- Make sure permissions are declared in AndroidManifest.xml
- Check that the permission request is triggered in MainActivity
- Verify Health Connect is properly installed

## Next Steps

1. **Run the app** and test temperature recording
2. **Review the documentation** in the `/docs` folder
3. **Explore the code** following the Clean Architecture layers
4. **Add features** like data retrieval or visualization

## Documentation

Comprehensive documentation is available in the `/docs` directory:
- Requirements & Functional Specification
- Acceptance Criteria
- Method-Level API Documentation
- Architecture Overview
- Flow Diagrams
- Implementation Guide

## Support

For issues or questions:
1. Check the documentation in `/docs`
2. Review the troubleshooting section above
3. Consult the [Health Connect documentation](https://developer.android.com/health-and-fitness/guides/health-connect)

---

**Status**: ✅ Project Setup Complete  
**Version**: 1.0  
**Base Package**: `com.eic.healthconnectdemo`
