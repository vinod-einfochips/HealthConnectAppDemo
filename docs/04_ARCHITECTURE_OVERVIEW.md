# Health Connect Body Temperature Tracker - Architecture Overview

## Document Information
- **Project**: Health Connect Body Temperature Tracker
- **Base Package**: `com.eic.healthconnectdemo`
- **Architecture Pattern**: MVVM + Clean Architecture
- **Version**: 1.0
- **Last Updated**: October 2025

---

## Table of Contents
1. [Architecture Principles](#1-architecture-principles)
2. [Layer Architecture](#2-layer-architecture)
3. [Component Diagram](#3-component-diagram)
4. [Package Structure](#4-package-structure)
5. [Data Flow](#5-data-flow)
6. [Dependency Management](#6-dependency-management)
7. [Design Patterns](#7-design-patterns)
8. [Technology Stack](#8-technology-stack)

---

## 1. Architecture Principles

### 1.1 Core Principles

#### Separation of Concerns
Each layer has a distinct responsibility and does not overlap with others:
- **Presentation**: UI rendering and user interaction
- **Domain**: Business logic and rules
- **Data**: Data access and external API communication

#### Dependency Rule
Dependencies point inward toward the domain layer:
```
Presentation → Domain ← Data
```
- Presentation depends on Domain
- Data depends on Domain
- Domain depends on nothing (pure Kotlin)

#### Single Responsibility
Each class has one reason to change:
- ViewModels manage UI state
- Use Cases encapsulate business logic
- Repositories handle data operations

#### Testability
All components are designed for easy testing:
- Interface-based design for mocking
- Dependency injection
- Pure functions where possible

---

## 2. Layer Architecture

### 2.1 Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                     PRESENTATION LAYER                       │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Composable UI (TemperatureInputScreen)              │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │  ViewModel (TemperatureViewModel)                    │  │
│  │  - Manages UI State (StateFlow)                      │  │
│  │  - Handles User Events                               │  │
│  │  - Coordinates Use Cases                             │  │
│  └────────────────────┬─────────────────────────────────┘  │
└────────────────────────┼─────────────────────────────────────┘
                         │
                         │ Uses
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                           │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Use Cases (Business Logic)                          │  │
│  │  ┌────────────────────────────────────────────────┐  │  │
│  │  │  RecordTemperatureUseCase                      │  │  │
│  │  │  CheckHealthConnectAvailabilityUseCase         │  │  │
│  │  │  RequestPermissionsUseCase                     │  │  │
│  │  └────────────────────┬───────────────────────────┘  │  │
│  └───────────────────────┼──────────────────────────────┘  │
│                          │                                   │
│  ┌───────────────────────▼──────────────────────────────┐  │
│  │  Domain Models (Pure Kotlin)                         │  │
│  │  - TemperatureRecord                                 │  │
│  │  - TemperatureUnit                                   │  │
│  │  - Result<T>                                         │  │
│  └──────────────────────────────────────────────────────┘  │
│                          ▲                                   │
│  ┌───────────────────────┼──────────────────────────────┐  │
│  │  Repository Interface                                │  │
│  │  - HealthConnectRepository                           │  │
│  └───────────────────────┬──────────────────────────────┘  │
└────────────────────────────┼───────────────────────────────┘
                         │ Implements
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                        DATA LAYER                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Repository Implementation                           │  │
│  │  - HealthConnectRepositoryImpl                       │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │  Data Mappers                                        │  │
│  │  - HealthConnectMapper (Domain ↔ SDK)               │  │
│  └────────────────────┬─────────────────────────────────┘  │
│                       │                                      │
│  ┌────────────────────▼─────────────────────────────────┐  │
│  │  Health Connect Manager                              │  │
│  │  - HealthConnectManager                              │  │
│  └────────────────────┬─────────────────────────────────┘  │
└────────────────────────┼─────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                   EXTERNAL DEPENDENCIES                      │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Health Connect SDK                                  │  │
│  │  - HealthConnectClient                               │  │
│  │  - BodyTemperatureRecord                             │  │
│  │  - Permission Controller                             │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

---

## 3. Component Diagram

### 3.1 Detailed Component Interaction

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER INTERFACE                           │
└────────────────────────┬────────────────────────────────────────┘
                         │ User Actions (Click, Input)
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    TemperatureInputScreen                        │
│  - Collects uiState: StateFlow<TemperatureUiState>             │
│  - Displays temperature input field                             │
│  - Displays unit selector (Celsius/Fahrenheit)                  │
│  - Displays record button                                       │
│  - Shows loading/error/success states                           │
└────────────────────────┬────────────────────────────────────────┘
                         │ Calls ViewModel methods
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                    TemperatureViewModel                          │
│  Properties:                                                     │
│    - uiState: StateFlow<TemperatureUiState>                    │
│    - viewModelScope: CoroutineScope                            │
│                                                                  │
│  Methods:                                                        │
│    - recordTemperature(temp, unit, timestamp)                  │
│    - checkPermissions()                                         │
│    - requestPermissions()                                       │
│    - validateTemperature(temp, unit)                           │
│    - clearError()                                               │
│                                                                  │
│  Dependencies:                                                   │
│    - RecordTemperatureUseCase                                  │
│    - CheckHealthConnectAvailabilityUseCase                     │
│    - RequestPermissionsUseCase                                 │
└────────────────────────┬────────────────────────────────────────┘
                         │ Invokes Use Cases
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   RecordTemperatureUseCase                       │
│  operator fun invoke(                                            │
│    temperature: Double,                                          │
│    unit: TemperatureUnit,                                       │
│    timestamp: Instant                                           │
│  ): Result<Unit>                                                │
│                                                                  │
│  Responsibilities:                                               │
│    1. Validate business rules                                   │
│    2. Create TemperatureRecord domain model                     │
│    3. Call repository                                           │
│    4. Handle errors                                             │
│                                                                  │
│  Dependencies:                                                   │
│    - HealthConnectRepository (interface)                        │
└────────────────────────┬────────────────────────────────────────┘
                         │ Calls repository interface
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│              HealthConnectRepository (Interface)                 │
│  suspend fun recordTemperature(record: TemperatureRecord)       │
│  suspend fun checkAvailability(): Result<Boolean>               │
│  suspend fun checkPermissions(permissions: Set<String>)         │
└────────────────────────┬────────────────────────────────────────┘
                         │ Implemented by
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│              HealthConnectRepositoryImpl                         │
│  Constructor:                                                    │
│    - healthConnectClient: HealthConnectClient                   │
│    - context: Context                                           │
│                                                                  │
│  Methods:                                                        │
│    - recordTemperature(record): Result<Unit>                   │
│      1. Map domain model to SDK model                          │
│      2. Call Health Connect API                                │
│      3. Handle SDK exceptions                                  │
│      4. Return Result                                          │
│                                                                  │
│    - checkAvailability(): Result<Boolean>                      │
│      1. Check SDK status                                       │
│      2. Return availability                                    │
│                                                                  │
│    - checkPermissions(permissions): Result<Boolean>            │
│      1. Query permission controller                            │
│      2. Compare granted vs requested                           │
│      3. Return status                                          │
│                                                                  │
│  Dependencies:                                                   │
│    - HealthConnectMapper                                        │
│    - HealthConnectManager                                       │
└────────────────────────┬────────────────────────────────────────┘
                         │ Uses
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   HealthConnectMapper                            │
│  Extension Functions:                                            │
│    - TemperatureRecord.toHealthConnectRecord()                  │
│      → BodyTemperatureRecord                                    │
│                                                                  │
│  Conversion Logic:                                               │
│    - Convert units to Celsius (Health Connect standard)        │
│    - Map timestamp to ZonedDateTime                            │
│    - Map measurement location                                   │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                   HealthConnectManager                           │
│  Wrapper for HealthConnectClient operations                      │
│                                                                  │
│  Methods:                                                        │
│    - insertRecord(record: Record): Result<Unit>                │
│    - getPermissionStatus(permissions): Map<Permission, Boolean>│
│                                                                  │
│  Error Handling:                                                 │
│    - Catches and wraps SDK exceptions                          │
│    - Provides consistent error types                           │
└────────────────────────┬────────────────────────────────────────┘
                         │ Delegates to
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                   HealthConnectClient (SDK)                      │
│  Android Health Connect SDK                                      │
│                                                                  │
│  Key APIs:                                                       │
│    - insertRecords(records: List<Record>)                      │
│    - permissionController.getGrantedPermissions()              │
│    - getSdkStatus(context)                                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 4. Package Structure

```
com.eic.healthconnectdemo/
│
├── presentation/
│   ├── ui/
│   │   ├── screen/
│   │   │   └── TemperatureInputScreen.kt
│   │   ├── component/
│   │   │   ├── TemperatureInputField.kt
│   │   │   ├── UnitSelector.kt
│   │   │   └── RecordButton.kt
│   │   └── theme/
│   │       ├── Color.kt
│   │       ├── Theme.kt
│   │       └── Type.kt
│   │
│   ├── viewmodel/
│   │   └── TemperatureViewModel.kt
│   │
│   └── state/
│       └── TemperatureUiState.kt
│
├── domain/
│   ├── model/
│   │   ├── TemperatureRecord.kt
│   │   ├── TemperatureUnit.kt
│   │   └── Result.kt
│   │
│   ├── usecase/
│   │   ├── RecordTemperatureUseCase.kt
│   │   ├── CheckHealthConnectAvailabilityUseCase.kt
│   │   └── RequestPermissionsUseCase.kt
│   │
│   └── repository/
│       └── HealthConnectRepository.kt (interface)
│
├── data/
│   ├── repository/
│   │   └── HealthConnectRepositoryImpl.kt
│   │
│   ├── mapper/
│   │   └── HealthConnectMapper.kt
│   │
│   └── manager/
│       └── HealthConnectManager.kt
│
├── di/
│   ├── AppModule.kt
│   ├── DataModule.kt
│   ├── DomainModule.kt
│   └── ViewModelModule.kt
│
└── util/
    ├── TemperatureValidator.kt
    ├── Constants.kt
    └── Extensions.kt
```

---

## 5. Data Flow

### 5.1 Temperature Recording Flow

```
┌─────────────────────────────────────────────────────────────────┐
│ STEP 1: User Input                                               │
└─────────────────────────────────────────────────────────────────┘
User enters: 37.5°C
User clicks: "Record Temperature"
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 2: UI Event Handling                                        │
└─────────────────────────────────────────────────────────────────┘
TemperatureInputScreen.onRecordClick()
         │
         ▼
viewModel.recordTemperature(37.5, CELSIUS, now())
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 3: ViewModel Processing                                     │
└─────────────────────────────────────────────────────────────────┘
TemperatureViewModel.recordTemperature() {
    // Update UI state to loading
    _uiState.update { it.copy(isLoading = true) }
    
    // Validate input
    if (!validateTemperature(37.5, CELSIUS)) {
        _uiState.update { it.copy(error = "Invalid temperature") }
        return
    }
    
    // Launch coroutine
    viewModelScope.launch {
        val result = recordTemperatureUseCase(37.5, CELSIUS, now())
        
        // Update UI state based on result
        _uiState.update {
            when (result) {
                is Success -> it.copy(isLoading = false, isSuccess = true)
                is Error -> it.copy(isLoading = false, error = result.message)
            }
        }
    }
}
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 4: Use Case Execution                                       │
└─────────────────────────────────────────────────────────────────┘
RecordTemperatureUseCase.invoke(37.5, CELSIUS, now()) {
    // Validate business rules
    require(temperature in 35.0..42.0) { "Invalid temperature" }
    require(timestamp <= now()) { "Future timestamp not allowed" }
    
    // Create domain model
    val record = TemperatureRecord(
        temperature = 37.5,
        unit = CELSIUS,
        timestamp = now()
    )
    
    // Call repository
    return repository.recordTemperature(record)
}
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 5: Repository Operation                                     │
└─────────────────────────────────────────────────────────────────┘
HealthConnectRepositoryImpl.recordTemperature(record) {
    return withContext(Dispatchers.IO) {
        try {
            // Map domain model to SDK model
            val sdkRecord = record.toHealthConnectRecord()
            
            // Insert into Health Connect
            healthConnectClient.insertRecords(listOf(sdkRecord))
            
            Result.Success(Unit)
        } catch (e: SecurityException) {
            Result.Error(HealthConnectException.PermissionDenied())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 6: Health Connect SDK                                       │
└─────────────────────────────────────────────────────────────────┘
HealthConnectClient.insertRecords([
    BodyTemperatureRecord(
        time = 2025-10-30T17:33:00Z,
        temperature = Temperature.celsius(37.5)
    )
])
         │
         ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 7: Result Propagation                                       │
└─────────────────────────────────────────────────────────────────┘
Result.Success(Unit)
         │
         ▼ (flows back through layers)
Use Case returns Result.Success
         │
         ▼
ViewModel updates UI state: isSuccess = true
         │
         ▼
UI displays success message
```

### 5.2 State Flow Diagram

```
┌─────────────┐
│   Initial   │
│   State     │
└──────┬──────┘
       │
       │ User enters temperature
       ▼
┌─────────────┐
│   Ready     │
│   State     │
└──────┬──────┘
       │
       │ User clicks "Record"
       ▼
┌─────────────┐
│  Loading    │
│   State     │
└──────┬──────┘
       │
       ├─────────────┐
       │             │
       │ Success     │ Error
       ▼             ▼
┌─────────────┐  ┌─────────────┐
│  Success    │  │   Error     │
│   State     │  │   State     │
└──────┬──────┘  └──────┬──────┘
       │                │
       │ Auto-dismiss   │ User dismisses
       │ after 3s       │ error
       ▼                ▼
┌─────────────┐  ┌─────────────┐
│   Ready     │  │   Ready     │
│   State     │  │   State     │
└─────────────┘  └─────────────┘
```

---

## 6. Dependency Management

### 6.1 Dependency Injection (Hilt)

```kotlin
// AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context
    
    @Provides
    @Singleton
    fun provideHealthConnectClient(
        context: Context
    ): HealthConnectClient {
        return HealthConnectClient.getOrCreate(context)
    }
}

// DataModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideHealthConnectManager(
        client: HealthConnectClient
    ): HealthConnectManager {
        return HealthConnectManager(client)
    }
    
    @Provides
    @Singleton
    fun provideHealthConnectRepository(
        client: HealthConnectClient,
        context: Context
    ): HealthConnectRepository {
        return HealthConnectRepositoryImpl(client, context)
    }
}

// DomainModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    
    @Provides
    fun provideRecordTemperatureUseCase(
        repository: HealthConnectRepository
    ): RecordTemperatureUseCase {
        return RecordTemperatureUseCase(repository)
    }
    
    @Provides
    fun provideCheckHealthConnectAvailabilityUseCase(
        repository: HealthConnectRepository
    ): CheckHealthConnectAvailabilityUseCase {
        return CheckHealthConnectAvailabilityUseCase(repository)
    }
}

// ViewModelModule.kt
@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    // ViewModels are automatically provided by Hilt
}
```

### 6.2 Dependency Graph

```
Application
    │
    ├─→ HealthConnectClient (Singleton)
    │       │
    │       ├─→ HealthConnectManager (Singleton)
    │       │
    │       └─→ HealthConnectRepositoryImpl (Singleton)
    │               │
    │               └─→ HealthConnectRepository (Interface)
    │                       │
    │                       ├─→ RecordTemperatureUseCase
    │                       ├─→ CheckHealthConnectAvailabilityUseCase
    │                       └─→ RequestPermissionsUseCase
    │                               │
    │                               └─→ TemperatureViewModel (Scoped)
    │                                       │
    │                                       └─→ TemperatureInputScreen
    │
    └─→ Context (Singleton)
```

---

## 7. Design Patterns

### 7.1 Patterns Used

#### Repository Pattern
- **Purpose**: Abstract data source details
- **Implementation**: `HealthConnectRepository` interface with `HealthConnectRepositoryImpl`
- **Benefits**: Easy to mock, swap implementations, test

#### Use Case Pattern
- **Purpose**: Encapsulate business logic
- **Implementation**: `RecordTemperatureUseCase`, etc.
- **Benefits**: Reusable, testable, single responsibility

#### MVVM Pattern
- **Purpose**: Separate UI from business logic
- **Implementation**: `TemperatureViewModel` + `TemperatureInputScreen`
- **Benefits**: Lifecycle-aware, testable, reactive

#### Observer Pattern
- **Purpose**: Reactive UI updates
- **Implementation**: `StateFlow` for state management
- **Benefits**: Automatic UI updates, lifecycle-safe

#### Mapper Pattern
- **Purpose**: Convert between layer models
- **Implementation**: `HealthConnectMapper`
- **Benefits**: Decoupled layers, clear boundaries

#### Result Pattern
- **Purpose**: Type-safe error handling
- **Implementation**: `Result<T>` sealed class
- **Benefits**: Explicit error handling, no exceptions in happy path

---

## 8. Technology Stack

### 8.1 Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 1.9.0+ | Primary language |
| Coroutines | 1.7.3 | Asynchronous programming |
| Flow | 1.7.3 | Reactive streams |
| Jetpack Compose | 1.5.4 | UI framework |
| Health Connect SDK | 1.1.0-alpha07 | Health data API |
| Hilt | 2.48 | Dependency injection |
| ViewModel | 2.6.2 | UI state management |

### 8.2 Build Configuration

```gradle
// build.gradle.kts (app level)
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.eic.healthconnectdemo"
    compileSdk = 34
    
    defaultConfig {
        applicationId = "com.eic.healthconnectdemo"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
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
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("io.mockk:mockk:1.13.8")
}
```

---

## 9. Quality Attributes

### 9.1 Maintainability
- **Modularity**: Clear separation of concerns
- **Documentation**: Comprehensive inline and external docs
- **Code Style**: Follows Kotlin conventions
- **Testability**: High test coverage possible

### 9.2 Scalability
- **Extensibility**: Easy to add new features
- **Flexibility**: Interface-based design
- **Reusability**: Use cases can be reused

### 9.3 Performance
- **Async Operations**: Non-blocking UI
- **Efficient State Management**: StateFlow with minimal recompositions
- **Resource Management**: Proper lifecycle handling

### 9.4 Security
- **Permission Handling**: Explicit permission requests
- **Data Validation**: Input validation at multiple layers
- **Error Handling**: No sensitive data in logs

---

## 10. Testing Strategy

### 10.1 Unit Tests

```kotlin
// ViewModel Tests
class TemperatureViewModelTest {
    @Test
    fun `recordTemperature with valid input updates state to success`()
    
    @Test
    fun `recordTemperature with invalid input shows error`()
    
    @Test
    fun `validateTemperature returns true for valid celsius`()
}

// Use Case Tests
class RecordTemperatureUseCaseTest {
    @Test
    fun `invoke with valid data calls repository`()
    
    @Test
    fun `invoke with invalid data throws exception`()
}

// Repository Tests
class HealthConnectRepositoryImplTest {
    @Test
    fun `recordTemperature calls Health Connect client`()
    
    @Test
    fun `recordTemperature handles permission error`()
}
```

### 10.2 Integration Tests

```kotlin
class TemperatureRecordingIntegrationTest {
    @Test
    fun `end to end temperature recording flow`()
}
```

### 10.3 UI Tests

```kotlin
@Composable
class TemperatureInputScreenTest {
    @Test
    fun `entering valid temperature enables record button`()
    
    @Test
    fun `clicking record shows loading state`()
}
```

---

## 11. Future Enhancements

### 11.1 Architectural Improvements
- Multi-module architecture
- Feature modules
- Shared core module

### 11.2 Additional Layers
- Local caching with Room
- Offline support with WorkManager
- Data synchronization

### 11.3 Advanced Features
- Historical data retrieval
- Data visualization
- Export functionality

---

## 12. References

- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Health Connect Documentation](https://developer.android.com/health-and-fitness/guides/health-connect)
- [Kotlin Coroutines Best Practices](https://kotlinlang.org/docs/coroutines-guide.html)

---

## Document Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | Oct 2025 | Architecture Team | Initial architecture document |
