# Health Connect Body Temperature Tracker - Flow Diagrams

## Document Information
- **Project**: Health Connect Body Temperature Tracker
- **Base Package**: `com.eic.healthconnectdemo`
- **Version**: 1.0
- **Last Updated**: October 2025

---

## Table of Contents
1. [End-to-End Temperature Recording Flow](#1-end-to-end-temperature-recording-flow)
2. [Permission Request Flow](#2-permission-request-flow)
3. [Health Connect Availability Check Flow](#3-health-connect-availability-check-flow)
4. [Error Handling Flow](#4-error-handling-flow)
5. [State Management Flow](#5-state-management-flow)

---

## 1. End-to-End Temperature Recording Flow

### 1.1 Complete Flow Diagram (ViewModel → Repository → Health Connect API)

```mermaid
sequenceDiagram
    actor User
    participant UI as TemperatureInputScreen
    participant VM as TemperatureViewModel
    participant UC as RecordTemperatureUseCase
    participant Repo as HealthConnectRepository
    participant Mapper as HealthConnectMapper
    participant Manager as HealthConnectManager
    participant HC as HealthConnectClient
    participant API as Health Connect API

    User->>UI: Enters temperature (37.5°C)
    User->>UI: Clicks "Record" button
    
    UI->>VM: recordTemperature(37.5, CELSIUS, now())
    
    activate VM
    VM->>VM: validateTemperature(37.5, CELSIUS)
    alt Invalid Temperature
        VM->>UI: Update state (error: "Invalid temperature")
        UI->>User: Display error message
    else Valid Temperature
        VM->>VM: Update state (isLoading: true)
        VM->>UI: Emit loading state
        UI->>User: Show loading indicator
        
        VM->>UC: invoke(37.5, CELSIUS, now())
        activate UC
        
        UC->>UC: Validate business rules
        UC->>UC: Create TemperatureRecord
        
        UC->>Repo: recordTemperature(record)
        activate Repo
        
        Repo->>Mapper: record.toHealthConnectRecord()
        activate Mapper
        Mapper->>Mapper: Convert to BodyTemperatureRecord
        Mapper-->>Repo: BodyTemperatureRecord
        deactivate Mapper
        
        Repo->>Manager: insertRecord(sdkRecord)
        activate Manager
        
        Manager->>HC: insertRecords([sdkRecord])
        activate HC
        
        HC->>API: Write temperature data
        activate API
        API-->>HC: Success response
        deactivate API
        
        HC-->>Manager: Success
        deactivate HC
        
        Manager-->>Repo: Result.Success(Unit)
        deactivate Manager
        
        Repo-->>UC: Result.Success(Unit)
        deactivate Repo
        
        UC-->>VM: Result.Success(Unit)
        deactivate UC
        
        VM->>VM: Update state (isSuccess: true, isLoading: false)
        VM->>UI: Emit success state
        UI->>User: Display success message
        
        Note over UI,User: Auto-dismiss after 3 seconds
        VM->>VM: resetState()
        VM->>UI: Emit ready state
        UI->>User: Clear input, ready for next entry
    end
    deactivate VM
```

---

## 2. Permission Request Flow

### 2.1 Permission Check and Request Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as TemperatureInputScreen
    participant VM as TemperatureViewModel
    participant UC as RequestPermissionsUseCase
    participant Repo as HealthConnectRepository
    participant HC as HealthConnectClient
    participant System as Android System

    User->>UI: Opens app
    UI->>VM: checkPermissions()
    
    activate VM
    VM->>UC: invoke(WRITE_BODY_TEMPERATURE)
    activate UC
    
    UC->>Repo: checkPermissions({WRITE_BODY_TEMPERATURE})
    activate Repo
    
    Repo->>HC: permissionController.getGrantedPermissions()
    activate HC
    HC-->>Repo: Set<Permission>
    deactivate HC
    
    Repo->>Repo: Compare requested vs granted
    
    alt Permission Already Granted
        Repo-->>UC: Result.Success(true)
        deactivate Repo
        UC-->>VM: Result.Success(true)
        deactivate UC
        VM->>VM: Update state (permissionGranted: true)
        VM->>UI: Emit permission granted state
        UI->>User: Enable temperature recording
    else Permission Not Granted
        Repo-->>UC: Result.Success(false)
        deactivate Repo
        UC-->>VM: Result.Success(false)
        deactivate UC
        VM->>VM: Update state (permissionGranted: false)
        VM->>UI: Emit permission denied state
        UI->>User: Show "Grant Permission" button
        
        User->>UI: Clicks "Grant Permission"
        UI->>VM: requestPermissions()
        
        activate VM
        VM->>UC: invoke(WRITE_BODY_TEMPERATURE)
        activate UC
        
        UC->>Repo: requestPermissions({WRITE_BODY_TEMPERATURE})
        activate Repo
        
        Repo->>HC: permissionController.createRequestPermissionResultContract()
        activate HC
        HC->>System: Launch permission screen
        deactivate HC
        
        System->>User: Display Health Connect permission dialog
        
        alt User Grants Permission
            User->>System: Grants permission
            System->>HC: Permission granted callback
            activate HC
            HC-->>Repo: Granted
            deactivate HC
            Repo-->>UC: Result.Success(true)
            deactivate Repo
            UC-->>VM: Result.Success(true)
            deactivate UC
            VM->>VM: Update state (permissionGranted: true)
            VM->>UI: Emit permission granted state
            UI->>User: Enable temperature recording
        else User Denies Permission
            User->>System: Denies permission
            System->>HC: Permission denied callback
            activate HC
            HC-->>Repo: Denied
            deactivate HC
            Repo-->>UC: Result.Success(false)
            deactivate Repo
            UC-->>VM: Result.Success(false)
            deactivate UC
            VM->>VM: Update state (permissionGranted: false, error: "Permission required")
            VM->>UI: Emit error state
            UI->>User: Show rationale and "Open Settings" button
        end
        deactivate VM
    end
    deactivate VM
```

---

## 3. Health Connect Availability Check Flow

### 3.1 Availability Verification Flow

```mermaid
sequenceDiagram
    actor User
    participant UI as TemperatureInputScreen
    participant VM as TemperatureViewModel
    participant UC as CheckHealthConnectAvailabilityUseCase
    participant Repo as HealthConnectRepository
    participant HC as HealthConnectClient
    participant System as Android System

    User->>UI: Opens app
    UI->>VM: onCreate() / LaunchedEffect
    
    activate VM
    VM->>UC: invoke()
    activate UC
    
    UC->>Repo: checkAvailability()
    activate Repo
    
    Repo->>HC: getSdkStatus(context)
    activate HC
    HC->>System: Query Health Connect status
    System-->>HC: SDK Status
    
    alt SDK_AVAILABLE
        HC-->>Repo: SDK_AVAILABLE
        deactivate HC
        Repo-->>UC: Result.Success(true)
        deactivate Repo
        UC-->>VM: Result.Success(true)
        deactivate UC
        VM->>VM: Update state (healthConnectAvailable: true)
        VM->>UI: Emit available state
        UI->>User: Show temperature input screen
        
    else SDK_UNAVAILABLE
        HC-->>Repo: SDK_UNAVAILABLE
        deactivate HC
        Repo-->>UC: Result.Success(false)
        deactivate Repo
        UC-->>VM: Result.Success(false)
        deactivate UC
        VM->>VM: Update state (healthConnectAvailable: false, error: "Health Connect not installed")
        VM->>UI: Emit unavailable state
        UI->>User: Show error with "Install Health Connect" button
        
        User->>UI: Clicks "Install Health Connect"
        UI->>System: Open Play Store (Health Connect)
        System->>User: Display Health Connect in Play Store
        
    else SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
        HC-->>Repo: SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
        deactivate HC
        Repo-->>UC: Result.Success(false)
        deactivate Repo
        UC-->>VM: Result.Success(false)
        deactivate UC
        VM->>VM: Update state (healthConnectAvailable: false, error: "Health Connect update required")
        VM->>UI: Emit update required state
        UI->>User: Show error with "Update Health Connect" button
    end
    deactivate VM
```

---

## 4. Error Handling Flow

### 4.1 Comprehensive Error Handling

```mermaid
flowchart TD
    Start([User Records Temperature]) --> Validate{Validate Input}
    
    Validate -->|Invalid| ShowError1[Show Validation Error]
    ShowError1 --> End1([User Corrects Input])
    
    Validate -->|Valid| CheckPermission{Check Permission}
    
    CheckPermission -->|Denied| RequestPerm[Request Permission]
    RequestPerm --> PermResult{Permission Result}
    PermResult -->|Granted| CallUseCase
    PermResult -->|Denied| ShowError2[Show Permission Error]
    ShowError2 --> End2([User Opens Settings])
    
    CheckPermission -->|Granted| CallUseCase[Call RecordTemperatureUseCase]
    
    CallUseCase --> CallRepo[Call Repository]
    CallRepo --> CallHC[Call Health Connect API]
    
    CallHC --> HCResult{API Result}
    
    HCResult -->|Success| UpdateSuccess[Update State: Success]
    UpdateSuccess --> ShowSuccess[Show Success Message]
    ShowSuccess --> AutoDismiss[Auto-dismiss after 3s]
    AutoDismiss --> Reset[Reset to Ready State]
    Reset --> End3([Ready for Next Entry])
    
    HCResult -->|SecurityException| HandleSecurity[Handle Permission Error]
    HandleSecurity --> ShowError3[Show: Permission Revoked]
    ShowError3 --> End4([Prompt Re-grant])
    
    HCResult -->|IOException| HandleNetwork[Handle Network Error]
    HandleNetwork --> ShowError4[Show: Network Error]
    ShowError4 --> RetryOption1{User Action}
    RetryOption1 -->|Retry| CallUseCase
    RetryOption1 -->|Cancel| End5([Keep Input Data])
    
    HCResult -->|IllegalStateException| HandleState[Handle State Error]
    HandleState --> ShowError5[Show: Health Connect Unavailable]
    ShowError5 --> End6([Check HC Status])
    
    HCResult -->|Unknown Exception| HandleUnknown[Handle Unknown Error]
    HandleUnknown --> LogError[Log Error Details]
    LogError --> ShowError6[Show: Generic Error]
    ShowError6 --> RetryOption2{User Action}
    RetryOption2 -->|Retry| CallUseCase
    RetryOption2 -->|Cancel| End7([Keep Input Data])
    
    style Start fill:#e1f5ff
    style End1 fill:#ffe1e1
    style End2 fill:#ffe1e1
    style End3 fill:#e1ffe1
    style End4 fill:#ffe1e1
    style End5 fill:#fff4e1
    style End6 fill:#ffe1e1
    style End7 fill:#fff4e1
    style ShowSuccess fill:#e1ffe1
```

---

## 5. State Management Flow

### 5.1 UI State Transitions

```mermaid
stateDiagram-v2
    [*] --> Initial: App Launch
    
    Initial --> CheckingAvailability: Check Health Connect
    
    CheckingAvailability --> Unavailable: HC Not Available
    CheckingAvailability --> CheckingPermission: HC Available
    
    Unavailable --> [*]: User Exits
    
    CheckingPermission --> PermissionDenied: Permission Not Granted
    CheckingPermission --> Ready: Permission Granted
    
    PermissionDenied --> RequestingPermission: User Requests Permission
    RequestingPermission --> PermissionDenied: Denied
    RequestingPermission --> Ready: Granted
    
    Ready --> Validating: User Enters Temperature
    
    Validating --> ValidationError: Invalid Input
    Validating --> Ready: Valid Input
    
    ValidationError --> Ready: User Corrects Input
    
    Ready --> Loading: User Clicks Record
    
    Loading --> Success: API Success
    Loading --> Error: API Error
    
    Success --> Ready: Auto-dismiss (3s)
    
    Error --> Ready: User Dismisses Error
    Error --> Loading: User Retries
    
    Ready --> [*]: User Exits

    note right of Initial
        isLoading: false
        isSuccess: false
        error: null
        permissionGranted: false
        healthConnectAvailable: false
    end note
    
    note right of Ready
        isLoading: false
        isSuccess: false
        error: null
        permissionGranted: true
        healthConnectAvailable: true
    end note
    
    note right of Loading
        isLoading: true
        isSuccess: false
        error: null
    end note
    
    note right of Success
        isLoading: false
        isSuccess: true
        error: null
    end note
    
    note right of Error
        isLoading: false
        isSuccess: false
        error: "Error message"
    end note
```

---

## 6. Detailed Layer Interaction Flow

### 6.1 Layer-by-Layer Data Flow

```mermaid
graph TB
    subgraph "Presentation Layer"
        A[User Input: 37.5°C] --> B[TemperatureInputScreen]
        B --> C[onClick Event]
        C --> D[TemperatureViewModel]
        D --> E{Validate Input}
        E -->|Invalid| F[Update State: Error]
        E -->|Valid| G[Update State: Loading]
        G --> H[Launch Coroutine]
    end
    
    subgraph "Domain Layer"
        H --> I[RecordTemperatureUseCase.invoke]
        I --> J{Validate Business Rules}
        J -->|Invalid| K[Throw IllegalArgumentException]
        J -->|Valid| L[Create TemperatureRecord]
        L --> M[Call Repository Interface]
    end
    
    subgraph "Data Layer"
        M --> N[HealthConnectRepositoryImpl]
        N --> O[Map Domain to SDK Model]
        O --> P[HealthConnectMapper.toHealthConnectRecord]
        P --> Q[BodyTemperatureRecord]
        Q --> R[HealthConnectManager.insertRecord]
        R --> S[HealthConnectClient.insertRecords]
    end
    
    subgraph "External API"
        S --> T[Health Connect API]
        T --> U{API Response}
        U -->|Success| V[Return Success]
        U -->|Error| W[Throw Exception]
    end
    
    subgraph "Response Flow"
        V --> X[Result.Success Unit]
        W --> Y[Result.Error Exception]
        X --> Z[Propagate to Repository]
        Y --> Z
        Z --> AA[Propagate to Use Case]
        AA --> AB[Propagate to ViewModel]
        AB --> AC{Result Type}
        AC -->|Success| AD[Update State: Success]
        AC -->|Error| AE[Update State: Error]
        AD --> AF[Emit State to UI]
        AE --> AF
        AF --> AG[UI Updates]
        AG --> AH[User Sees Result]
    end
    
    style A fill:#e1f5ff
    style AH fill:#e1ffe1
    style K fill:#ffe1e1
    style W fill:#ffe1e1
    style AE fill:#ffe1e1
```

---

## 7. Coroutine Flow

### 7.1 Coroutine Context and Threading

```mermaid
sequenceDiagram
    participant Main as Main Thread
    participant VM as ViewModel (viewModelScope)
    participant IO as IO Dispatcher
    participant Default as Default Dispatcher
    participant HC as Health Connect API

    Main->>VM: User clicks Record button
    activate VM
    Note over VM: Running on Main thread
    
    VM->>VM: viewModelScope.launch
    Note over VM: Coroutine started in Main context
    
    VM->>VM: Update UI state (isLoading = true)
    Note over VM: UI update on Main thread
    
    VM->>IO: withContext(Dispatchers.IO)
    activate IO
    Note over IO: Switch to IO thread
    
    IO->>Default: Validate temperature
    activate Default
    Note over Default: CPU-intensive validation
    Default-->>IO: Validation result
    deactivate Default
    
    IO->>HC: repository.recordTemperature()
    activate HC
    Note over HC: Network/API call on IO thread
    HC-->>IO: Result
    deactivate HC
    
    IO-->>VM: Return result
    deactivate IO
    Note over VM: Back to Main context
    
    VM->>VM: Update UI state (isSuccess/isError)
    Note over VM: UI update on Main thread
    
    VM->>Main: Emit state to UI
    deactivate VM
    Note over Main: UI recomposes
```

---

## 8. Complete Use Case Flow Chart

### 8.1 End-to-End User Journey

```mermaid
flowchart TD
    Start([User Opens App]) --> Init[Initialize ViewModel]
    Init --> CheckHC{Health Connect Available?}
    
    CheckHC -->|No| ShowHCError[Show HC Unavailable Error]
    ShowHCError --> InstallHC[Prompt to Install HC]
    InstallHC --> End1([User Installs HC])
    
    CheckHC -->|Yes| CheckPerm{Permission Granted?}
    
    CheckPerm -->|No| ShowPermPrompt[Show Permission Prompt]
    ShowPermPrompt --> RequestPerm[Request Permission]
    RequestPerm --> PermGranted{Permission Granted?}
    PermGranted -->|No| ShowPermError[Show Permission Error]
    ShowPermError --> End2([User Opens Settings])
    PermGranted -->|Yes| ShowInput
    
    CheckPerm -->|Yes| ShowInput[Show Temperature Input Screen]
    
    ShowInput --> UserEnters[User Enters Temperature]
    UserEnters --> SelectUnit[User Selects Unit]
    SelectUnit --> ValidateInput{Input Valid?}
    
    ValidateInput -->|No| ShowValidationError[Show Validation Error]
    ShowValidationError --> UserEnters
    
    ValidateInput -->|Yes| EnableButton[Enable Record Button]
    EnableButton --> UserClicks[User Clicks Record]
    
    UserClicks --> ShowLoading[Show Loading State]
    ShowLoading --> CallVM[Call ViewModel.recordTemperature]
    CallVM --> CallUC[Call RecordTemperatureUseCase]
    CallUC --> CallRepo[Call Repository]
    CallRepo --> MapData[Map Domain to SDK Model]
    MapData --> CallAPI[Call Health Connect API]
    
    CallAPI --> APIResult{API Result?}
    
    APIResult -->|Success| ShowSuccess[Show Success Message]
    ShowSuccess --> ClearInput[Clear Input Field]
    ClearInput --> AutoDismiss[Auto-dismiss after 3s]
    AutoDismiss --> ShowInput
    
    APIResult -->|Error| DetermineError{Error Type?}
    
    DetermineError -->|Permission| ShowPermError2[Show Permission Error]
    ShowPermError2 --> End3([Re-request Permission])
    
    DetermineError -->|Network| ShowNetworkError[Show Network Error]
    ShowNetworkError --> RetryOption{User Choice?}
    RetryOption -->|Retry| CallVM
    RetryOption -->|Cancel| ShowInput
    
    DetermineError -->|Validation| ShowValError[Show Validation Error]
    ShowValError --> UserEnters
    
    DetermineError -->|Unknown| ShowGenericError[Show Generic Error]
    ShowGenericError --> LogError[Log Error Details]
    LogError --> RetryOption2{User Choice?}
    RetryOption2 -->|Retry| CallVM
    RetryOption2 -->|Cancel| ShowInput
    
    style Start fill:#e1f5ff
    style ShowSuccess fill:#e1ffe1
    style ShowHCError fill:#ffe1e1
    style ShowPermError fill:#ffe1e1
    style ShowValidationError fill:#ffe1e1
    style ShowNetworkError fill:#ffe1e1
    style ShowGenericError fill:#ffe1e1
```

---

## 9. Data Transformation Flow

### 9.1 Model Mapping Through Layers

```mermaid
graph LR
    subgraph "UI Layer"
        A[User Input<br/>temperature: String = '37.5'<br/>unit: String = 'Celsius']
    end
    
    subgraph "ViewModel"
        B[Parse Input<br/>temperature: Double = 37.5<br/>unit: TemperatureUnit.CELSIUS]
    end
    
    subgraph "Domain Model"
        C[TemperatureRecord<br/>temperature: Double = 37.5<br/>unit: TemperatureUnit.CELSIUS<br/>timestamp: Instant]
    end
    
    subgraph "Data Layer Mapper"
        D[Map to SDK Model<br/>Convert to Celsius if needed<br/>Create metadata]
    end
    
    subgraph "Health Connect SDK"
        E[BodyTemperatureRecord<br/>temperature: Temperature.celsius 37.5<br/>time: ZonedDateTime<br/>metadata: Metadata]
    end
    
    subgraph "Health Connect API"
        F[Persisted Record<br/>Stored in Health Connect database]
    end
    
    A -->|User submits| B
    B -->|Create domain model| C
    C -->|Map to SDK| D
    D -->|Create SDK record| E
    E -->|Insert| F
    
    style A fill:#e1f5ff
    style C fill:#fff4e1
    style E fill:#e1ffe1
    style F fill:#d4edda
```

---

## 10. How to Use These Diagrams

### 10.1 For Developers
- **Implementation Reference**: Use sequence diagrams to understand method call order
- **Debugging**: Follow flow diagrams to trace issues
- **Testing**: Use flows to create test scenarios

### 10.2 For Architects
- **Design Review**: Validate architectural decisions
- **Documentation**: Share with team for alignment
- **Onboarding**: Help new team members understand the system

### 10.3 For QA
- **Test Case Creation**: Use flows to create comprehensive test cases
- **Edge Case Identification**: Identify all possible paths
- **Acceptance Testing**: Verify all flows work as documented

---

## 11. Diagram Legend

### 11.1 Mermaid Diagram Conventions

**Sequence Diagrams:**
- `actor`: User/external entity
- `participant`: System component
- `activate/deactivate`: Component lifecycle
- `alt/else`: Conditional flows
- `Note`: Additional context

**Flowcharts:**
- `Rectangle`: Process/action
- `Diamond`: Decision point
- `Rounded Rectangle`: Start/end point
- `Parallelogram`: Input/output

**State Diagrams:**
- `State`: System state
- `Transition`: State change
- `Note`: State properties

---

## 12. Rendering Instructions

These diagrams use Mermaid syntax and can be rendered in:
- GitHub Markdown
- GitLab Markdown
- VS Code with Mermaid extension
- Mermaid Live Editor (https://mermaid.live)
- Confluence with Mermaid plugin
- Documentation sites (MkDocs, Docusaurus, etc.)

---

## Document Revision History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | Oct 2025 | Architecture Team | Initial flow diagrams |
