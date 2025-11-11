package com.eic.healthconnectdemo.presentation.state

import com.eic.healthconnectdemo.domain.model.TemperatureRecord

/**
 * UI state for the temperature history screen.
 */
data class TemperatureHistoryUiState(
    val isLoading: Boolean = false,
    val records: List<TemperatureRecord> = emptyList(),
    val error: String? = null,
    val isDeleting: Boolean = false
)
