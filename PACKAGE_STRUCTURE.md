# Package Structure Overview

## Complete Package Hierarchy

```
com.eic.healthconnectdemo/
│
├── 📱 MainActivity.kt                          # Main entry point
├── 🚀 HealthConnectApp.kt                      # Application class
│
├── 🎯 core/                                    # Core functionality (NEW)
│   │
│   ├── base/                                   # Base classes
│   │   ├── BaseActivity.kt                     # Common activity functionality
│   │   └── BaseViewModel.kt                    # Common ViewModel with error handling
│   │
│   ├── config/                                 # Configuration management
│   │   ├── AppConfig.kt                        # Central configuration access
│   │   └── Environment.kt                      # Environment enum (DEV/QA/PROD)
│   │
│   ├── logger/                                 # Logging utilities
│   │   └── AppLogger.kt                        # Environment-aware logger
│   │
│   ├── network/                                # Network utilities (for future)
│   │   └── NetworkResult.kt                    # Network result wrapper
│   │
│   └── util/                                   # Common utilities
│       ├── Constants.kt                        # App-wide constants
│       └── Extensions.kt                       # Extension functions
│
├── 💾 data/                                    # Data layer
│   │
│   ├── mapper/                                 # Data transformation
│   │   └── HealthConnectMapper.kt              # Health Connect ↔ Domain mapping
│   │
│   └── repository/                             # Repository implementations
│       └── HealthConnectRepositoryImpl.kt      # Health Connect data operations
│
├── 🎨 domain/                                  # Domain/Business layer
│   │
│   ├── model/                                  # Domain models
│   │   ├── Result.kt                           # Result wrapper (Success/Error)
│   │   ├── TemperatureRecord.kt                # Temperature domain model
│   │   └── TemperatureUnit.kt                  # Temperature unit enum
│   │
│   ├── repository/                             # Repository contracts
│   │   └── HealthConnectRepository.kt          # Repository interface
│   │
│   └── usecase/                                # Business logic
│       ├── CheckHealthConnectAvailabilityUseCase.kt
│       ├── DeleteTemperatureRecordUseCase.kt
│       ├── GetTemperatureHistoryUseCase.kt
│       └── RecordTemperatureUseCase.kt
│
├── 🖼️ presentation/                            # Presentation layer
│   │
│   ├── state/                                  # UI state models
│   │   ├── TemperatureHistoryUiState.kt        # History screen state
│   │   └── TemperatureUiState.kt               # Main screen state
│   │
│   ├── ui/                                     # UI components
│   │   ├── TemperatureHistoryActivity.kt       # History screen
│   │   └── TemperatureHistoryAdapter.kt        # RecyclerView adapter
│   │
│   └── viewmodel/                              # ViewModels
│       ├── TemperatureHistoryViewModel.kt      # History screen logic
│       └── TemperatureViewModel.kt             # Main screen logic
│
└── 💉 di/                                      # Dependency Injection
    └── AppModule.kt                            # Hilt DI configuration
```

## Layer Responsibilities

### 🎯 Core Layer (NEW)
**Purpose**: Shared utilities and infrastructure

**Responsibilities**:
- Environment configuration management
- Centralized logging
- Base classes for Activities and ViewModels
- Common utilities and extensions
- Network utilities (for future API integration)

**Key Files**:
- `AppConfig.kt` - Access environment settings
- `AppLogger.kt` - Environment-aware logging
- `Constants.kt` - App-wide constants
- `Extensions.kt` - Kotlin extensions

### 💾 Data Layer
**Purpose**: Data access and manipulation

**Responsibilities**:
- Implement repository interfaces
- Handle Health Connect SDK operations
- Transform data between layers
- Cache management (future)

**Key Files**:
- `HealthConnectRepositoryImpl.kt` - Health Connect operations
- `HealthConnectMapper.kt` - Data transformation

### 🎨 Domain Layer
**Purpose**: Business logic and rules

**Responsibilities**:
- Define domain models
- Define repository contracts
- Implement use cases (business logic)
- Validation rules

**Key Files**:
- `TemperatureRecord.kt` - Core domain model
- `HealthConnectRepository.kt` - Repository contract
- Use cases - Business logic encapsulation

### 🖼️ Presentation Layer
**Purpose**: UI and user interaction

**Responsibilities**:
- Display data to users
- Handle user input
- Manage UI state
- Navigate between screens

**Key Files**:
- Activities - Screen implementations
- ViewModels - UI logic and state management
- Adapters - List item rendering
- UI States - Screen state models

### 💉 DI Layer
**Purpose**: Dependency injection configuration

**Responsibilities**:
- Provide dependencies
- Manage object lifecycle
- Configure Hilt modules

**Key Files**:
- `AppModule.kt` - Hilt configuration

## Data Flow

```
┌─────────────────────────────────────────────────────────────┐
│                         User Action                          │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│  PRESENTATION LAYER                                          │
│  ┌──────────────┐         ┌──────────────┐                  │
│  │   Activity   │ ←────→  │  ViewModel   │                  │
│  └──────────────┘         └──────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│  DOMAIN LAYER                                                │
│  ┌──────────────┐         ┌──────────────┐                  │
│  │   Use Case   │ ←────→  │  Repository  │ (Interface)      │
│  └──────────────┘         │  Interface   │                  │
│                           └──────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│  DATA LAYER                                                  │
│  ┌──────────────┐         ┌──────────────┐                  │
│  │  Repository  │ ←────→  │    Mapper    │                  │
│  │     Impl     │         └──────────────┘                  │
│  └──────────────┘                                            │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│  EXTERNAL                                                    │
│  ┌──────────────────────────────────────────────┐            │
│  │         Health Connect SDK                   │            │
│  └──────────────────────────────────────────────┘            │
└─────────────────────────────────────────────────────────────┘
```

## Module Dependencies

```
┌──────────────┐
│ Presentation │
└──────┬───────┘
       │ depends on
       ↓
┌──────────────┐
│   Domain     │
└──────┬───────┘
       │ depends on
       ↓
┌──────────────┐
│     Data     │
└──────────────┘

All layers can use:
┌──────────────┐
│     Core     │
└──────────────┘
```

## File Naming Conventions

### Activities
- Pattern: `[Feature]Activity.kt`
- Examples: `MainActivity.kt`, `TemperatureHistoryActivity.kt`

### ViewModels
- Pattern: `[Feature]ViewModel.kt`
- Examples: `TemperatureViewModel.kt`, `TemperatureHistoryViewModel.kt`

### Use Cases
- Pattern: `[Action][Entity]UseCase.kt`
- Examples: `RecordTemperatureUseCase.kt`, `GetTemperatureHistoryUseCase.kt`

### Repositories
- Interface: `[Entity]Repository.kt`
- Implementation: `[Entity]RepositoryImpl.kt`
- Examples: `HealthConnectRepository.kt`, `HealthConnectRepositoryImpl.kt`

### UI States
- Pattern: `[Feature]UiState.kt`
- Examples: `TemperatureUiState.kt`, `TemperatureHistoryUiState.kt`

### Mappers
- Pattern: `[Source]Mapper.kt`
- Examples: `HealthConnectMapper.kt`

### Adapters
- Pattern: `[Feature]Adapter.kt`
- Examples: `TemperatureHistoryAdapter.kt`

## Adding New Features

### Step 1: Define Domain Model
Create model in `domain/model/`

### Step 2: Create Repository Interface
Add interface in `domain/repository/`

### Step 3: Implement Repository
Create implementation in `data/repository/`

### Step 4: Create Use Case
Add use case in `domain/usecase/`

### Step 5: Create UI State
Define state in `presentation/state/`

### Step 6: Create ViewModel
Implement ViewModel in `presentation/viewmodel/`

### Step 7: Create UI
Build Activity/Fragment in `presentation/ui/`

### Step 8: Wire with DI
Update `di/AppModule.kt` if needed

## Best Practices

1. ✅ **Keep layers independent** - Domain should not depend on Data or Presentation
2. ✅ **Use dependency injection** - Let Hilt manage dependencies
3. ✅ **Single responsibility** - Each class should have one clear purpose
4. ✅ **Use base classes** - Leverage BaseActivity and BaseViewModel
5. ✅ **Centralize constants** - Use Constants.kt
6. ✅ **Log appropriately** - Use AppLogger (respects environment)
7. ✅ **Handle errors gracefully** - Use Result wrapper
8. ✅ **Keep UI dumb** - Business logic belongs in ViewModels/UseCases
9. ✅ **Test each layer** - Unit tests for domain, integration tests for data
10. ✅ **Document complex logic** - Add KDoc comments

## Quick Reference

### Import Paths
```kotlin
// Core
import com.eic.healthconnectdemo.core.config.AppConfig
import com.eic.healthconnectdemo.core.logger.AppLogger
import com.eic.healthconnectdemo.core.util.Constants
import com.eic.healthconnectdemo.core.util.*

// Domain
import com.eic.healthconnectdemo.domain.model.*
import com.eic.healthconnectdemo.domain.repository.*
import com.eic.healthconnectdemo.domain.usecase.*

// Data
import com.eic.healthconnectdemo.data.repository.*
import com.eic.healthconnectdemo.data.mapper.*

// Presentation
import com.eic.healthconnectdemo.presentation.state.*
import com.eic.healthconnectdemo.presentation.viewmodel.*
import com.eic.healthconnectdemo.presentation.ui.*
```
