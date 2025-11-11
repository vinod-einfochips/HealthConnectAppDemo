package com.eic.healthconnectdemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.usecase.DeleteTemperatureRecordUseCase
import com.eic.healthconnectdemo.domain.usecase.GetTemperatureHistoryUseCase
import com.eic.healthconnectdemo.presentation.state.TemperatureHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing temperature history UI state and business logic.
 */
@HiltViewModel
class TemperatureHistoryViewModel @Inject constructor(
    private val getTemperatureHistoryUseCase: GetTemperatureHistoryUseCase,
    private val deleteTemperatureRecordUseCase: DeleteTemperatureRecordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TemperatureHistoryUiState())
    val uiState: StateFlow<TemperatureHistoryUiState> = _uiState.asStateFlow()

    init {
        loadTemperatureHistory()
    }

    /**
     * Loads temperature history from Health Connect.
     */
    fun loadTemperatureHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = getTemperatureHistoryUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            records = result.data
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = result.exception.message ?: "Failed to load temperature history"
                        )
                    }
                }
            }
        }
    }

    /**
     * Deletes a temperature record.
     */
    fun deleteRecord(recordId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, error = null) }

            when (val result = deleteTemperatureRecordUseCase(recordId)) {
                is Result.Success -> {
                    // Reload the list after successful deletion
                    loadTemperatureHistory()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            error = result.exception.message ?: "Failed to delete record"
                        )
                    }
                }
            }
        }
    }

    /**
     * Clears the current error state.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
