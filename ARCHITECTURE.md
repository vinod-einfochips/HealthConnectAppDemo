# HealthConnect Demo - Architecture Documentation

## Multi-Environment Setup

The application supports three environments with different configurations:

### Environments

1. **Development (dev)**
   - App ID: `com.eic.healthconnectdemo.dev`
   - App Name: "HealthConnect DEV"
   - Base URL: `https://dev-api.healthconnect.com`
   - Logging: Enabled
   - Can be installed alongside QA and Production builds

2. **QA (qa)**
   - App ID: `com.eic.healthconnectdemo.qa`
   - App Name: "HealthConnect QA"
   - Base URL: `https://qa-api.healthconnect.com`
   - Logging: Enabled
   - Can be installed alongside Dev and Production builds

3. **Production (prod)**
   - App ID: `com.eic.healthconnectdemo`
   - App Name: "HealthConnect"
   - Base URL: `https://api.healthconnect.com`
   - Logging: Disabled
   - Production-ready build

### Build Variants

The app generates the following build variants:
- `devDebug` - Development environment with debug build
- `devRelease` - Development environment with release build
- `qaDebug` - QA environment with debug build
- `qaRelease` - QA environment with release build
- `prodDebug` - Production environment with debug build
- `prodRelease` - Production environment with release build

### Building for Different Environments

```bash
# Build Dev Debug
./gradlew assembleDevDebug

# Build QA Release
./gradlew assembleQaRelease

# Build Production Release
./gradlew assembleProdRelease

# Install Dev Debug on device
./gradlew installDevDebug
```

## Package Structure

```
com.eic.healthconnectdemo/
├── core/                           # Core functionality
│   ├── base/                       # Base classes
│   │   ├── BaseActivity.kt         # Base activity with common functionality
│   │   └── BaseViewModel.kt        # Base ViewModel with error handling
│   ├── config/                     # Configuration
│   │   ├── AppConfig.kt            # Environment configuration
│   │   └── Environment.kt          # Environment enum
│   ├── logger/                     # Logging
│   │   └── AppLogger.kt            # Centralized logging utility
│   ├── network/                    # Network utilities (for future API)
│   │   └── NetworkResult.kt        # Network result wrapper
│   └── util/                       # Utilities
│       ├── Constants.kt            # App-wide constants
│       └── Extensions.kt           # Extension functions
│
├── data/                           # Data layer
│   ├── mapper/                     # Data mappers
│   │   └── HealthConnectMapper.kt  # Health Connect data mapping
│   └── repository/                 # Repository implementations
│       └── HealthConnectRepositoryImpl.kt
│
├── di/                             # Dependency Injection
│   └── AppModule.kt                # Hilt modules
│
├── domain/                         # Domain layer
│   ├── model/                      # Domain models
│   │   ├── Result.kt               # Result wrapper
│   │   ├── TemperatureRecord.kt    # Temperature domain model
│   │   └── TemperatureUnit.kt      # Temperature unit enum
│   ├── repository/                 # Repository interfaces
│   │   └── HealthConnectRepository.kt
│   └── usecase/                    # Use cases (business logic)
│       ├── CheckHealthConnectAvailabilityUseCase.kt
│       ├── DeleteTemperatureRecordUseCase.kt
│       ├── GetTemperatureHistoryUseCase.kt
│       └── RecordTemperatureUseCase.kt
│
├── presentation/                   # Presentation layer
│   ├── state/                      # UI states
│   │   ├── TemperatureHistoryUiState.kt
│   │   └── TemperatureUiState.kt
│   ├── ui/                         # UI components
│   │   ├── TemperatureHistoryActivity.kt
│   │   └── TemperatureHistoryAdapter.kt
│   └── viewmodel/                  # ViewModels
│       ├── TemperatureHistoryViewModel.kt
│       └── TemperatureViewModel.kt
│
├── HealthConnectApp.kt             # Application class
└── MainActivity.kt                 # Main activity
```

## Architecture Layers

### 1. Core Layer
Contains shared utilities, base classes, and configuration:
- **Base Classes**: Common functionality for Activities and ViewModels
- **Config**: Environment-specific configuration
- **Logger**: Centralized logging that respects environment settings
- **Network**: Network utilities for future API integration
- **Util**: Extension functions and constants

### 2. Data Layer
Handles data operations:
- **Mappers**: Convert between data models and domain models
- **Repository Implementations**: Concrete implementations of repository interfaces

### 3. Domain Layer
Contains business logic and domain models:
- **Models**: Pure domain entities
- **Repository Interfaces**: Define contracts for data operations
- **Use Cases**: Encapsulate business logic

### 4. Presentation Layer
Handles UI and user interactions:
- **UI States**: Represent UI state
- **UI Components**: Activities, Fragments, Adapters
- **ViewModels**: Manage UI state and business logic

### 5. Dependency Injection (DI)
Hilt modules for dependency injection configuration.

## Key Features

### Environment Configuration
- Access configuration via `AppConfig` object
- Environment-specific settings (URLs, logging, etc.)
- Build-time configuration using BuildConfig

### Logging
- Centralized logging via `AppLogger`
- Automatically disabled in production
- Tagged logging for easy filtering

### Base Classes
- `BaseActivity`: Common activity functionality
- `BaseViewModel`: Error handling and logging

### Extension Functions
- Date/time formatting
- Temperature conversion
- Toast helpers

### Constants
- Centralized constants for easy maintenance
- Organized by category (HealthConnect, Temperature, DateTime, UI)

## Usage Examples

### Accessing Configuration
```kotlin
// Check current environment
if (AppConfig.isDevelopment()) {
    // Dev-specific code
}

// Get base URL
val url = AppConfig.baseUrl

// Check if logging is enabled
if (AppConfig.isLoggingEnabled) {
    // Log something
}
```

### Using Logger
```kotlin
// Log messages
AppLogger.d("MyTag", "Debug message")
AppLogger.i("MyTag", "Info message")
AppLogger.e("MyTag", "Error message", exception)
```

### Using Extensions
```kotlin
// Format dates
val dateString = instant.toDateString()
val timeString = instant.toTimeString()

// Convert temperature
val fahrenheit = celsius.celsiusToFahrenheit()

// Show toast
context.showToast("Message")
```

### Using Base Classes
```kotlin
class MyViewModel @Inject constructor() : BaseViewModel() {
    
    fun loadData() {
        launchSafe {
            // Coroutine with automatic error handling
            logDebug("Loading data...")
        }
    }
}
```

## Testing Different Environments

1. **Switch Build Variant** in Android Studio:
   - Go to `Build > Select Build Variant`
   - Choose desired variant (e.g., `devDebug`, `qaRelease`)

2. **Run the app** - it will use the selected environment configuration

3. **Install multiple variants** on the same device for testing

## Future Enhancements

- Add Retrofit for API calls using `NetworkResult`
- Add Room database for local caching
- Add feature modules for better scalability
- Add analytics integration
- Add crash reporting (Firebase Crashlytics)
- Add remote configuration (Firebase Remote Config)
