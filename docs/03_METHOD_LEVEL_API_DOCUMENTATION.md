# Health Connect Body Temperature Tracker - Method-Level API Documentation

## Document Information
- **Project**: Health Connect Body Temperature Tracker
- **Base Package**: `com.eic.healthconnectdemo`
- **Version**: 1.0
- **Last Updated**: October 2025

---

## Table of Contents
1. [Presentation Layer](#1-presentation-layer)
2. [Domain Layer](#2-domain-layer)
3. [Data Layer](#3-data-layer)
4. [Models](#4-models)
5. [Utilities](#5-utilities)

---

## 1. Presentation Layer

### 1.1 TemperatureViewModel

**Package**: `com.eic.healthconnectdemo.presentation.viewmodel`

**Description**: ViewModel responsible for managing UI state and handling user interactions for temperature recording.

#### Properties

```kotlin
val uiState: StateFlow<TemperatureUiState>
```
- **Type**: `StateFlow<TemperatureUiState>`
- **Description**: Exposes the current UI state as a cold flow
- **Thread Safety**: Thread-safe, can be collected from any coroutine context
- **Lifecycle**: Survives configuration changes

```kotlin
private val _uiState: MutableStateFlow<TemperatureUiState>
```
- **Type**: `MutableStateFlow<TemperatureUiState>`
- **Description**: Internal mutable state holder
- **Access**: Private, only accessible within ViewModel

#### Methods

##### recordTemperature()
```kotlin
fun recordTemperature(
    temperature: Double,
    unit: TemperatureUnit,
    timestamp: Instant = Clock.System.now()
)
```

**Description**: Records a body temperature measurement to Health Connect.

**Parameters**:
- `temperature: Double` - The temperature value to record
- `unit: TemperatureUnit` - The unit of measurement (CELSIUS or FAHRENHEIT)
- `timestamp: Instant` - The time of measurement (defaults to current time)

**Behavior**:
1. Validates input temperature against unit-specific ranges
2. Updates UI state to loading
3. Invokes `RecordTemperatureUseCase`
4. Updates UI state based on result (success/error)
5. Handles all exceptions gracefully

**Side Effects**:
- Updates `_uiState` to reflect loading, success, or error states
- Launches coroutine in `viewModelScope`

**Error Handling**:
- Catches `IllegalArgumentException` for validation errors
- Catches `SecurityException` for permission errors
- Catches generic `Exception` for unexpected errors

**Example**:
```kotlin
viewModel.recordTemperature(
    temperature = 37.5,
    unit = TemperatureUnit.CELSIUS
)
```

---

##### validateTemperature()
```kotlin
private fun validateTemperature(temperature: Double, unit: TemperatureUnit): Boolean
```

**Description**: Validates temperature value against acceptable ranges for the given unit.

**Parameters**:
- `temperature: Double` - The temperature value to validate
- `unit: TemperatureUnit` - The unit of measurement

**Returns**: `Boolean`
- `true` if temperature is within valid range
- `false` if temperature is out of range

**Validation Rules**:
- **Celsius**: 35.0°C to 42.0°C
- **Fahrenheit**: 95.0°F to 107.6°F

**Thread Safety**: Safe to call from any thread

**Example**:
```kotlin
val isValid = validateTemperature(37.5, TemperatureUnit.CELSIUS) // true
val isInvalid = validateTemperature(50.0, TemperatureUnit.CELSIUS) // false
```

---

##### checkPermissions()
```kotlin
fun checkPermissions()
```

**Description**: Checks if the app has the required Health Connect permissions.

**Parameters**: None

**Behavior**:
1. Invokes `CheckPermissionsUseCase`
2. Updates UI state with permission status
3. Triggers permission request if not granted

**Side Effects**:
- Updates `_uiState.permissionGranted`
- May trigger permission request flow

**Coroutine Context**: Runs in `viewModelScope`

---

##### requestPermissions()
```kotlin
fun requestPermissions()
```

**Description**: Initiates the Health Connect permission request flow.

**Parameters**: None

**Behavior**:
1. Invokes `RequestPermissionsUseCase`
2. Launches Health Connect permission screen
3. Waits for user response
4. Updates UI state based on result

**Side Effects**:
- Launches external Health Connect permission activity
- Updates `_uiState.permissionGranted`

**Note**: This method requires an Activity context to launch the permission screen

---

##### clearError()
```kotlin
fun clearError()
```

**Description**: Clears the current error state in the UI.

**Parameters**: None

**Behavior**:
- Sets `_uiState.error` to `null`
- Resets UI to ready state

**Thread Safety**: Safe to call from any thread

---

##### resetState()
```kotlin
fun resetState()
```

**Description**: Resets the ViewModel to its initial state.

**Parameters**: None

**Behavior**:
- Clears all input values
- Resets error state
- Resets loading state
- Maintains permission status

**Use Case**: Called after successful recording or when user navigates away

---

### 1.2 TemperatureUiState

**Package**: `com.eic.healthconnectdemo.presentation.state`

**Description**: Data class representing the UI state for temperature recording screen.

#### Properties

```kotlin
data class TemperatureUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val permissionGranted: Boolean = false,
    val healthConnectAvailable: Boolean = false,
    val temperatureValue: String = "",
    val selectedUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)
```

**Property Descriptions**:

- `isLoading: Boolean` - Indicates if an operation is in progress
- `isSuccess: Boolean` - Indicates if the last operation succeeded
- `error: String?` - Contains error message if an error occurred
- `permissionGranted: Boolean` - Indicates if Health Connect permission is granted
- `healthConnectAvailable: Boolean` - Indicates if Health Connect is available on device
- `temperatureValue: String` - Current temperature input value
- `selectedUnit: TemperatureUnit` - Currently selected temperature unit

---

## 2. Domain Layer

### 2.1 RecordTemperatureUseCase

**Package**: `com.eic.healthconnectdemo.domain.usecase`

**Description**: Use case encapsulating the business logic for recording body temperature to Health Connect.

#### Methods

##### invoke()
```kotlin
suspend operator fun invoke(
    temperature: Double,
    unit: TemperatureUnit,
    timestamp: Instant
): Result<Unit>
```

**Description**: Executes the temperature recording operation.

**Parameters**:
- `temperature: Double` - The temperature value to record
- `unit: TemperatureUnit` - The unit of measurement
- `timestamp: Instant` - The time of measurement

**Returns**: `Result<Unit>`
- `Result.Success<Unit>` if recording succeeds
- `Result.Error<Unit>` if recording fails

**Behavior**:
1. Validates input parameters
2. Creates domain model `TemperatureRecord`
3. Delegates to repository for persistence
4. Wraps result in `Result` type

**Exceptions**:
- Throws `IllegalArgumentException` if temperature is invalid
- Throws `IllegalArgumentException` if timestamp is in the future

**Coroutine Context**: Suspending function, runs in caller's context

**Example**:
```kotlin
val result = recordTemperatureUseCase(
    temperature = 37.5,
    unit = TemperatureUnit.CELSIUS,
    timestamp = Clock.System.now()
)

when (result) {
    is Result.Success -> println("Temperature recorded")
    is Result.Error -> println("Error: ${result.exception.message}")
}
```

---

### 2.2 CheckHealthConnectAvailabilityUseCase

**Package**: `com.eic.healthconnectdemo.domain.usecase`

**Description**: Use case for checking if Health Connect is available on the device.

#### Methods

##### invoke()
```kotlin
suspend operator fun invoke(): Result<Boolean>
```

**Description**: Checks Health Connect availability.

**Parameters**: None

**Returns**: `Result<Boolean>`
- `Result.Success(true)` if Health Connect is available
- `Result.Success(false)` if Health Connect is not available
- `Result.Error` if check fails

**Behavior**:
1. Queries Health Connect SDK availability
2. Checks device compatibility
3. Returns availability status

**Thread Safety**: Safe to call from any coroutine context

---

### 2.3 RequestPermissionsUseCase

**Package**: `com.eic.healthconnectdemo.domain.usecase`

**Description**: Use case for requesting Health Connect permissions.

#### Methods

##### invoke()
```kotlin
suspend operator fun invoke(
    permissions: Set<String>
): Result<Boolean>
```

**Description**: Requests specified Health Connect permissions.

**Parameters**:
- `permissions: Set<String>` - Set of permission strings to request

**Returns**: `Result<Boolean>`
- `Result.Success(true)` if all permissions granted
- `Result.Success(false)` if any permission denied
- `Result.Error` if request fails

**Behavior**:
1. Validates permission set is not empty
2. Launches Health Connect permission screen
3. Waits for user response
4. Returns aggregated result

**Required Permissions**:
```kotlin
setOf("android.permission.health.WRITE_BODY_TEMPERATURE")
```

---

## 3. Data Layer

### 3.1 HealthConnectRepository (Interface)

**Package**: `com.eic.healthconnectdemo.data.repository`

**Description**: Repository interface defining Health Connect data operations.

#### Methods

##### recordTemperature()
```kotlin
suspend fun recordTemperature(record: TemperatureRecord): Result<Unit>
```

**Description**: Records a temperature measurement to Health Connect.

**Parameters**:
- `record: TemperatureRecord` - The temperature record to save

**Returns**: `Result<Unit>`
- `Result.Success<Unit>` if recording succeeds
- `Result.Error<Unit>` if recording fails

**Contract**: Implementation must handle all Health Connect API exceptions

---

##### checkAvailability()
```kotlin
suspend fun checkAvailability(): Result<Boolean>
```

**Description**: Checks if Health Connect is available.

**Parameters**: None

**Returns**: `Result<Boolean>` - Availability status

---

##### checkPermissions()
```kotlin
suspend fun checkPermissions(permissions: Set<String>): Result<Boolean>
```

**Description**: Checks if specified permissions are granted.

**Parameters**:
- `permissions: Set<String>` - Permissions to check

**Returns**: `Result<Boolean>` - True if all permissions granted

---

### 3.2 HealthConnectRepositoryImpl

**Package**: `com.eic.healthconnectdemo.data.repository`

**Description**: Implementation of HealthConnectRepository using Health Connect SDK.

#### Constructor

```kotlin
class HealthConnectRepositoryImpl(
    private val healthConnectClient: HealthConnectClient,
    private val context: Context
) : HealthConnectRepository
```

**Parameters**:
- `healthConnectClient: HealthConnectClient` - Health Connect SDK client
- `context: Context` - Application context

---

#### Methods

##### recordTemperature()
```kotlin
override suspend fun recordTemperature(record: TemperatureRecord): Result<Unit>
```

**Description**: Records temperature to Health Connect.

**Implementation Details**:
1. Converts domain model to Health Connect `BodyTemperatureRecord`
2. Calls `healthConnectClient.insertRecords()`
3. Handles SDK-specific exceptions
4. Returns wrapped result

**Error Handling**:
- `SecurityException` → Permission denied
- `IOException` → Network/connectivity issue
- `IllegalStateException` → Health Connect not available
- `Exception` → Generic error

**Example**:
```kotlin
val record = TemperatureRecord(
    temperature = 37.5,
    unit = TemperatureUnit.CELSIUS,
    timestamp = Clock.System.now()
)

val result = repository.recordTemperature(record)
```

---

##### checkAvailability()
```kotlin
override suspend fun checkAvailability(): Result<Boolean>
```

**Description**: Checks Health Connect availability.

**Implementation Details**:
1. Calls `HealthConnectClient.getSdkStatus(context)`
2. Checks if status is `SDK_AVAILABLE`
3. Returns availability status

**Possible SDK Statuses**:
- `SDK_AVAILABLE` → Health Connect is available
- `SDK_UNAVAILABLE` → Health Connect not installed
- `SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED` → Update required

---

##### checkPermissions()
```kotlin
override suspend fun checkPermissions(permissions: Set<String>): Result<Boolean>
```

**Description**: Checks permission status.

**Implementation Details**:
1. Converts permission strings to `Permission` objects
2. Calls `healthConnectClient.permissionController.getGrantedPermissions()`
3. Compares requested vs granted permissions
4. Returns true only if all requested permissions are granted

---

### 3.3 HealthConnectManager

**Package**: `com.eic.healthconnectdemo.data.manager`

**Description**: Wrapper class for Health Connect client operations.

#### Methods

##### insertRecord()
```kotlin
suspend fun insertRecord(record: Record): Result<Unit>
```

**Description**: Inserts a single record into Health Connect.

**Parameters**:
- `record: Record` - Health Connect record to insert

**Returns**: `Result<Unit>`

**Implementation**:
```kotlin
return try {
    healthConnectClient.insertRecords(listOf(record))
    Result.Success(Unit)
} catch (e: Exception) {
    Result.Error(e)
}
```

---

##### getPermissionStatus()
```kotlin
suspend fun getPermissionStatus(permissions: Set<Permission>): Map<Permission, Boolean>
```

**Description**: Gets the grant status for multiple permissions.

**Parameters**:
- `permissions: Set<Permission>` - Permissions to check

**Returns**: `Map<Permission, Boolean>` - Map of permission to grant status

---

## 4. Models

### 4.1 TemperatureRecord

**Package**: `com.eic.healthconnectdemo.domain.model`

**Description**: Domain model representing a body temperature measurement.

```kotlin
data class TemperatureRecord(
    val temperature: Double,
    val unit: TemperatureUnit,
    val timestamp: Instant,
    val measurementLocation: String? = null,
    val metadata: RecordMetadata? = null
)
```

**Properties**:
- `temperature: Double` - Temperature value (must be positive)
- `unit: TemperatureUnit` - Unit of measurement
- `timestamp: Instant` - Time of measurement (UTC)
- `measurementLocation: String?` - Optional location (e.g., "oral", "armpit")
- `metadata: RecordMetadata?` - Optional Health Connect metadata

**Validation**:
- Temperature must be within valid range for unit
- Timestamp cannot be in the future

---

### 4.2 TemperatureUnit

**Package**: `com.eic.healthconnectdemo.domain.model`

**Description**: Enum representing temperature units.

```kotlin
enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT;
    
    fun getValidRange(): ClosedFloatingPointRange<Double> {
        return when (this) {
            CELSIUS -> 35.0..42.0
            FAHRENHEIT -> 95.0..107.6
        }
    }
    
    fun getSymbol(): String {
        return when (this) {
            CELSIUS -> "°C"
            FAHRENHEIT -> "°F"
        }
    }
}
```

**Methods**:

##### getValidRange()
```kotlin
fun getValidRange(): ClosedFloatingPointRange<Double>
```
**Returns**: Valid temperature range for the unit

##### getSymbol()
```kotlin
fun getSymbol(): String
```
**Returns**: Unit symbol (°C or °F)

---

### 4.3 Result (Sealed Class)

**Package**: `com.eic.healthconnectdemo.domain.model`

**Description**: Sealed class representing operation results.

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    
    inline fun <R> map(transform: (T) -> R): Result<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
        }
    }
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (Exception) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
}
```

---

## 5. Utilities

### 5.1 TemperatureValidator

**Package**: `com.eic.healthconnectdemo.util`

**Description**: Utility class for temperature validation.

#### Methods

##### isValid()
```kotlin
fun isValid(temperature: Double, unit: TemperatureUnit): Boolean
```

**Description**: Validates temperature against unit-specific ranges.

**Parameters**:
- `temperature: Double` - Temperature to validate
- `unit: TemperatureUnit` - Unit of measurement

**Returns**: `Boolean` - True if valid

**Example**:
```kotlin
TemperatureValidator.isValid(37.5, TemperatureUnit.CELSIUS) // true
TemperatureValidator.isValid(50.0, TemperatureUnit.CELSIUS) // false
```

---

##### getErrorMessage()
```kotlin
fun getErrorMessage(temperature: Double, unit: TemperatureUnit): String?
```

**Description**: Returns error message if temperature is invalid.

**Parameters**:
- `temperature: Double` - Temperature to validate
- `unit: TemperatureUnit` - Unit of measurement

**Returns**: `String?` - Error message or null if valid

---

### 5.2 HealthConnectMapper

**Package**: `com.eic.healthconnectdemo.data.mapper`

**Description**: Maps between domain models and Health Connect SDK models.

#### Methods

##### toHealthConnectRecord()
```kotlin
fun TemperatureRecord.toHealthConnectRecord(): BodyTemperatureRecord
```

**Description**: Converts domain model to Health Connect SDK model.

**Receiver**: `TemperatureRecord`

**Returns**: `BodyTemperatureRecord`

**Implementation**:
```kotlin
fun TemperatureRecord.toHealthConnectRecord(): BodyTemperatureRecord {
    return BodyTemperatureRecord(
        time = timestamp,
        temperature = Temperature.celsius(
            if (unit == TemperatureUnit.CELSIUS) temperature
            else (temperature - 32) * 5 / 9
        ),
        measurementLocation = measurementLocation?.let {
            BodyTemperatureMeasurementLocation.fromString(it)
        }
    )
}
```

**Note**: Always converts to Celsius for Health Connect storage

---

## 6. Exception Handling

### Custom Exceptions

#### HealthConnectException
```kotlin
sealed class HealthConnectException(message: String) : Exception(message) {
    class NotAvailable : HealthConnectException("Health Connect is not available")
    class PermissionDenied : HealthConnectException("Permission denied")
    class InvalidData : HealthConnectException("Invalid data provided")
    class NetworkError : HealthConnectException("Network error occurred")
}
```

---

## 7. Threading and Coroutines

### Dispatcher Usage

- **Main**: UI updates, ViewModel state changes
- **IO**: Health Connect API calls, repository operations
- **Default**: CPU-intensive validation, data transformation

### Example
```kotlin
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true) }
    
    val result = withContext(Dispatchers.IO) {
        repository.recordTemperature(record)
    }
    
    _uiState.update { 
        it.copy(
            isLoading = false,
            isSuccess = result is Result.Success
        )
    }
}
```

---

## 8. Testing Considerations

### Mockable Interfaces
- `HealthConnectRepository` - Mock for ViewModel tests
- `HealthConnectClient` - Mock for Repository tests

### Test Utilities
```kotlin
object TestDataFactory {
    fun createTemperatureRecord(
        temperature: Double = 37.5,
        unit: TemperatureUnit = TemperatureUnit.CELSIUS
    ): TemperatureRecord
}
```

---

## 9. Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0 | Oct 2025 | Initial documentation |

---

## 10. References

- [Health Connect API Reference](https://developer.android.com/reference/kotlin/androidx/health/connect/client/package-summary)
- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
