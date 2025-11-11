package com.eic.healthconnectdemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import com.eic.healthconnectdemo.domain.usecase.CheckHealthConnectAvailabilityUseCase
import com.eic.healthconnectdemo.domain.usecase.RecordTemperatureUseCase
import com.eic.healthconnectdemo.presentation.state.TemperatureUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

/**
 * ViewModel for managing temperature recording UI state and business logic.
 */
@HiltViewModel
class TemperatureViewModel @Inject constructor(
    private val recordTemperatureUseCase: RecordTemperatureUseCase,
    private val checkHealthConnectAvailabilityUseCase: CheckHealthConnectAvailabilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TemperatureUiState())
    val uiState: StateFlow<TemperatureUiState> = _uiState.asStateFlow()

    init {
        checkHealthConnectAvailability()
    }

    /**
     * Checks if Health Connect is available on the device.
     */
    private fun checkHealthConnectAvailability() {
        viewModelScope.launch {
            when (val result = checkHealthConnectAvailabilityUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(healthConnectAvailable = result.data) }
                    if (!result.data) {
                        _uiState.update {
                            it.copy(error = "Health Connect is not available on this device")
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            healthConnectAvailable = false,
                            error = "Failed to check Health Connect availability"
                        )
                    }
                }
            }
        }
    }

    /**
     * Records a temperature measurement to Health Connect.
     *
     * @param temperature The temperature value
     * @param unit The unit of measurement
     * @param timestamp The time of measurement (defaults to current time)
     */
    fun recordTemperature(
        temperature: Double,
        unit: TemperatureUnit,
        timestamp: Instant = Clock.System.now()
    ) {
        viewModelScope.launch {
            // Validate input
            if (!validateTemperature(temperature, unit)) {
                _uiState.update {
                    it.copy(
                        error = "Temperature must be between ${unit.getValidRange().start} and ${unit.getValidRange().endInclusive} ${unit.getSymbol()}"
                    )
                }
                return@launch
            }

            // Update to loading state
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Record temperature
            when (val result = recordTemperatureUseCase(temperature, unit, timestamp)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            temperatureValue = ""
                        )
                    }

                    // Auto-dismiss success message after 3 seconds
                    delay(3000)
                    _uiState.update { it.copy(isSuccess = false) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Failed to record temperature"
                        )
                    }
                }
            }
        }
    }

    /**
     * Validates temperature against unit-specific ranges.
     */
    private fun validateTemperature(temperature: Double, unit: TemperatureUnit): Boolean {
        return temperature in unit.getValidRange()
    }

    /**
     * Updates the temperature input value.
     */
    fun updateTemperatureValue(value: String) {
        _uiState.update { it.copy(temperatureValue = value, error = null) }
    }

    /**
     * Updates the selected temperature unit.
     */
    fun updateSelectedUnit(unit: TemperatureUnit) {
        _uiState.update { it.copy(selectedUnit = unit) }
    }

    /**
     * Clears the current error state.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    /**
     * Sets the permission granted status.
     */
    fun setPermissionGranted(granted: Boolean) {
        _uiState.update { it.copy(permissionGranted = granted) }
    }
}
