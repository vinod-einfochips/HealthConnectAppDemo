# Health Connect Body Temperature Tracker

## Project Overview

This project demonstrates how to implement a body temperature recording feature using Android's Health Connect API, following MVVM + Clean Architecture principles.

**Base Package**: `com.eic.healthconnectdemo`

---

## Use Case

**Health Connect Body Temperature Tracker (Health Connect Only)**

A focused implementation that allows users to record body temperature measurements directly into Health Connect, making the data available to other health and fitness applications in the Android ecosystem.

### Key Features
- ✅ Record body temperature to Health Connect
- ✅ Support for Celsius and Fahrenheit units
- ✅ Input validation
- ✅ Permission management
- ✅ Health Connect availability checking
- ✅ Error handling and user feedback

### Out of Scope
- ❌ Reading/retrieving temperature data
- ❌ Local database (Room) caching
- ❌ Data synchronization
- ❌ Historical data visualization

---

## Architecture

### Pattern
**MVVM (Model-View-ViewModel) + Clean Architecture**

### Layer Structure
```
Presentation Layer (UI + ViewModel)
         ↓
Domain Layer (Use Cases + Models)
         ↓
Data Layer (Repository + Mappers)
         ↓
Health Connect API
```

### Technology Stack
- **Language**: Kotlin 1.9.0+
- **UI**: Jetpack Compose
- **Concurrency**: Kotlin Coroutines + Flow
- **DI**: Hilt
- **Health API**: Health Connect SDK 1.1.0-alpha07
- **Min SDK**: API 28 (Android 9.0)
- **Target SDK**: API 34+

---

## Documentation

Comprehensive documentation is available in the `/docs` directory:

### 📋 Core Documentation

1. **[Requirements & Functional Specification](docs/01_REQUIREMENTS_AND_FUNCTIONAL_SPEC.md)**
   - Business requirements
   - Functional specifications
   - Technical requirements
   - Data models
   - Validation rules

2. **[Acceptance Criteria](docs/02_ACCEPTANCE_CRITERIA.md)**
   - Detailed acceptance criteria for all features
   - Test scenarios
   - Definition of done
   - Sign-off checklist

3. **[Method-Level API Documentation](docs/03_METHOD_LEVEL_API_DOCUMENTATION.md)**
   - Complete API reference
   - Method signatures and descriptions
   - Parameters and return types
   - Usage examples
   - Error handling

4. **[Architecture Overview](docs/04_ARCHITECTURE_OVERVIEW.md)**
   - Architecture principles
   - Layer architecture
   - Component diagrams
   - Package structure
   - Design patterns
   - Dependency management

5. **[Flow Diagrams](docs/05_FLOW_DIAGRAMS.md)**
   - End-to-end temperature recording flow
   - Permission request flow
   - Health Connect availability check
   - Error handling flow
   - State management flow
   - Mermaid diagrams (ViewModel → Repository → Health Connect API)

6. **[Implementation Guide](docs/06_IMPLEMENTATION_GUIDE.md)**
   - Step-by-step implementation instructions
   - Code examples
   - Testing guide
   - Troubleshooting
   - Best practices

---

## Quick Start

### Prerequisites
```bash
- Android Studio Arctic Fox (2020.3.1) or later
- Kotlin 1.9.0+
- Android device/emulator with API 28+
- Health Connect installed on device
```

### Setup Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd HealthConnectDemo
   ```

2. **Open in Android Studio**
   - Open the project in Android Studio
   - Sync Gradle dependencies

3. **Install Health Connect**
   - Ensure Health Connect is installed on your test device
   - Available on devices with Android 14+ or via Play Store for older versions

4. **Run the app**
   - Build and run the application
   - Grant necessary permissions when prompted

---

## Project Structure

```
com.eic.healthconnectdemo/
│
├── presentation/           # UI Layer
│   ├── ui/
│   │   └── screen/        # Composable screens
│   ├── viewmodel/         # ViewModels
│   └── state/             # UI state models
│
├── domain/                # Business Logic Layer
│   ├── model/             # Domain models
│   ├── usecase/           # Use cases
│   └── repository/        # Repository interfaces
│
├── data/                  # Data Layer
│   ├── repository/        # Repository implementations
│   ├── mapper/            # Data mappers
│   └── manager/           # Health Connect manager
│
├── di/                    # Dependency Injection
│   └── AppModule.kt       # Hilt modules
│
└── util/                  # Utilities
    └── Constants.kt
```

---

## Key Components

### Domain Layer
- **TemperatureRecord**: Domain model for temperature data
- **TemperatureUnit**: Enum for Celsius/Fahrenheit
- **RecordTemperatureUseCase**: Business logic for recording
- **Result<T>**: Type-safe result wrapper

### Data Layer
- **HealthConnectRepository**: Repository interface
- **HealthConnectRepositoryImpl**: Implementation using Health Connect SDK
- **HealthConnectMapper**: Maps domain models to SDK models

### Presentation Layer
- **TemperatureViewModel**: Manages UI state and user interactions
- **TemperatureInputScreen**: Composable UI for temperature input
- **TemperatureUiState**: UI state data class

---

## Data Flow

```
User Input (37.5°C)
    ↓
TemperatureInputScreen
    ↓
TemperatureViewModel
    ↓ (validates input)
RecordTemperatureUseCase
    ↓ (business logic)
HealthConnectRepository
    ↓ (maps to SDK model)
HealthConnectClient
    ↓
Health Connect API
    ↓
Success/Error Response
    ↓ (propagates back)
UI Update
```

---

## Validation Rules

### Temperature Ranges
- **Celsius**: 35.0°C to 42.0°C
- **Fahrenheit**: 95.0°F to 107.6°F

### Business Rules
- Temperature must be within valid range for selected unit
- Timestamp cannot be in the future
- Health Connect must be available
- Write permission must be granted

---

## Permissions

### Required Permissions
```xml
<uses-permission android:name="android.permission.health.WRITE_BODY_TEMPERATURE"/>
```

### Permission Handling
- Runtime permission request
- Permission status checking
- Graceful handling of denied permissions
- Settings navigation for re-granting

---

## Error Handling

### Error Types
- **Validation Errors**: Invalid temperature input
- **Permission Errors**: Missing or denied permissions
- **Availability Errors**: Health Connect not available
- **Network Errors**: Connectivity issues
- **Unknown Errors**: Unexpected exceptions

### Error Strategy
- Type-safe error handling with `Result<T>` sealed class
- User-friendly error messages
- Retry mechanisms for transient errors
- Comprehensive logging for debugging

---

## Testing

### Unit Tests
- ViewModel state management
- Use case business logic
- Input validation
- Repository operations

### Integration Tests
- End-to-end recording flow
- Permission handling
- Error scenarios

### UI Tests
- User interactions
- State transitions
- Error display

---

## Dependencies

```gradle
// Health Connect
implementation("androidx.health.connect:connect-client:1.1.0-alpha07")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

// Hilt
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")
```

---

## Best Practices Implemented

1. **Clean Architecture**: Clear separation of concerns across layers
2. **SOLID Principles**: Single responsibility, dependency inversion
3. **Reactive Programming**: StateFlow for reactive UI updates
4. **Type Safety**: Result wrapper for error handling
5. **Testability**: Interface-based design for easy mocking
6. **Coroutines**: Non-blocking async operations
7. **Input Validation**: Multi-layer validation
8. **Error Handling**: Comprehensive error management

---

## Future Enhancements

### Planned Features
- Data retrieval from Health Connect
- Historical data visualization
- Offline support with local caching
- Batch recording
- Wearable device integration
- Export functionality
- Reminder notifications

---

## Resources

### Official Documentation
- [Health Connect Developer Guide](https://developer.android.com/health-and-fitness/guides/health-connect)
- [Health Connect API Reference](https://developer.android.com/reference/kotlin/androidx/health/connect/client/package-summary)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)

### Related Links
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [StateFlow Documentation](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/)

---

## Contributing

When contributing to this project:
1. **Read the [Code Style Guide](CODE_STYLE_GUIDE.md)** - Mandatory for all contributors
2. Run `./gradlew preCommitCheck` before committing
3. Follow the existing architecture patterns
4. Write unit tests for new features (60% coverage minimum)
5. Update documentation
6. Ensure all quality checks pass (ktlint, detekt, tests)
7. No TODO/FIXME comments in production code

---

## License

[Specify your license here]

---

## Contact

For questions or support, please contact [your contact information]

---

## Acknowledgments

This project demonstrates best practices for integrating with Android Health Connect API while maintaining clean, testable, and maintainable code architecture.

---

**Version**: 1.0  
**Last Updated**: October 2025  
**Status**: Documentation Complete - Ready for Implementation
