package com.eic.healthconnectdemo.presentation.state

import com.eic.healthconnectdemo.domain.model.TemperatureUnit

/**
 * UI state for the temperature input screen.
 *
 * @property isLoading Indicates if an operation is in progress
 * @property isSuccess Indicates if the last operation succeeded
 * @property error Contains error message if an error occurred
 * @property permissionGranted Indicates if Health Connect permission is granted
 * @property healthConnectAvailable Indicates if Health Connect is available on device
 * @property temperatureValue Current temperature input value
 * @property selectedUnit Currently selected temperature unit
 */
data class TemperatureUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val permissionGranted: Boolean = false,
    val healthConnectAvailable: Boolean = false,
    val temperatureValue: String = "",
    val selectedUnit: TemperatureUnit = TemperatureUnit.CELSIUS
)
