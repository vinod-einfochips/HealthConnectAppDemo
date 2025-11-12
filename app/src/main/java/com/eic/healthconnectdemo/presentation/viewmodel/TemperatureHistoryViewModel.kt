package com.eic.healthconnectdemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eic.healthconnectdemo.domain.model.DateRangeFilter
import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.SortOption
import com.eic.healthconnectdemo.domain.model.TemperatureRangeFilter
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
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
class TemperatureHistoryViewModel
    @Inject
    constructor(
        private val getTemperatureHistoryUseCase: GetTemperatureHistoryUseCase,
        private val deleteTemperatureRecordUseCase: DeleteTemperatureRecordUseCase,
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
                                allRecords = result.data,
                                filteredRecords =
                                    applyFiltersAndSort(
                                        result.data,
                                        it.dateRangeFilter,
                                        it.temperatureRangeFilter,
                                        it.sortOption,
                                    ),
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.exception.message ?: "Failed to load temperature history",
                            )
                        }
                    }
                }
            }
        }

        /**
         * Applies filters and sorting to the records.
         */
        private fun applyFiltersAndSort(
            records: List<TemperatureRecord>,
            dateFilter: DateRangeFilter,
            tempFilter: TemperatureRangeFilter,
            sortOption: SortOption,
        ): List<TemperatureRecord> {
            var filtered = records

            // Apply date range filter
            if (dateFilter != DateRangeFilter.ALL) {
                filtered = filtered.filter { dateFilter.matches(it.timestamp) }
            }

            // Apply temperature range filter
            if (tempFilter != TemperatureRangeFilter.ALL) {
                filtered = filtered.filter { tempFilter.matches(it) }
            }

            // Apply sorting
            return sortOption.sort(filtered)
        }

        /**
         * Updates the date range filter.
         */
        fun setDateRangeFilter(filter: DateRangeFilter) {
            _uiState.update {
                it.copy(
                    dateRangeFilter = filter,
                    filteredRecords =
                        applyFiltersAndSort(
                            it.allRecords,
                            filter,
                            it.temperatureRangeFilter,
                            it.sortOption,
                        ),
                )
            }
        }

        /**
         * Updates the temperature range filter.
         */
        fun setTemperatureRangeFilter(filter: TemperatureRangeFilter) {
            _uiState.update {
                it.copy(
                    temperatureRangeFilter = filter,
                    filteredRecords =
                        applyFiltersAndSort(
                            it.allRecords,
                            it.dateRangeFilter,
                            filter,
                            it.sortOption,
                        ),
                )
            }
        }

        /**
         * Updates the sort option.
         */
        fun setSortOption(option: SortOption) {
            _uiState.update {
                it.copy(
                    sortOption = option,
                    filteredRecords =
                        applyFiltersAndSort(
                            it.allRecords,
                            it.dateRangeFilter,
                            it.temperatureRangeFilter,
                            option,
                        ),
                )
            }
        }

        /**
         * Clears all filters and resets to default state.
         */
        fun clearFilters() {
            _uiState.update {
                it.copy(
                    dateRangeFilter = DateRangeFilter.ALL,
                    temperatureRangeFilter = TemperatureRangeFilter.ALL,
                    sortOption = SortOption.DATE_NEWEST_FIRST,
                    filteredRecords =
                        applyFiltersAndSort(
                            it.allRecords,
                            DateRangeFilter.ALL,
                            TemperatureRangeFilter.ALL,
                            SortOption.DATE_NEWEST_FIRST,
                        ),
                )
            }
        }

        /**
         * Toggles the filter panel expansion state.
         */
        fun toggleFilterPanel() {
            _uiState.update { it.copy(isFilterPanelExpanded = !it.isFilterPanelExpanded) }
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
                                error = result.exception.message ?: "Failed to delete record",
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
