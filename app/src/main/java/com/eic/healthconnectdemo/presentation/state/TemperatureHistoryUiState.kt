package com.eic.healthconnectdemo.presentation.state

import com.eic.healthconnectdemo.domain.model.DateRangeFilter
import com.eic.healthconnectdemo.domain.model.SortOption
import com.eic.healthconnectdemo.domain.model.TemperatureRangeFilter
import com.eic.healthconnectdemo.domain.model.TemperatureRecord

/**
 * UI state for the temperature history screen.
 */
data class TemperatureHistoryUiState(
    val isLoading: Boolean = false,
    val allRecords: List<TemperatureRecord> = emptyList(),
    val filteredRecords: List<TemperatureRecord> = emptyList(),
    val error: String? = null,
    val isDeleting: Boolean = false,
    val dateRangeFilter: DateRangeFilter = DateRangeFilter.ALL,
    val temperatureRangeFilter: TemperatureRangeFilter = TemperatureRangeFilter.ALL,
    val sortOption: SortOption = SortOption.DATE_NEWEST_FIRST,
    val isFilterPanelExpanded: Boolean = false
) {
    /**
     * Returns true if any filters are active (not default).
     */
    fun hasActiveFilters(): Boolean {
        return dateRangeFilter != DateRangeFilter.ALL ||
                temperatureRangeFilter != TemperatureRangeFilter.ALL ||
                sortOption != SortOption.DATE_NEWEST_FIRST
    }
}
