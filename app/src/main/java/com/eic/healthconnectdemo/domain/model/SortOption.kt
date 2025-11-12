package com.eic.healthconnectdemo.domain.model

/**
 * Enum representing sort options for temperature history.
 */
enum class SortOption {
    DATE_NEWEST_FIRST,
    DATE_OLDEST_FIRST,
    TEMPERATURE_HIGHEST_FIRST,
    TEMPERATURE_LOWEST_FIRST,
    ;

    /**
     * Returns the display name for this sort option.
     */
    fun getDisplayName(): String {
        return when (this) {
            DATE_NEWEST_FIRST -> "Date (Newest First)"
            DATE_OLDEST_FIRST -> "Date (Oldest First)"
            TEMPERATURE_HIGHEST_FIRST -> "Temperature (Highest First)"
            TEMPERATURE_LOWEST_FIRST -> "Temperature (Lowest First)"
        }
    }

    /**
     * Sorts a list of temperature records according to this option.
     */
    fun sort(records: List<TemperatureRecord>): List<TemperatureRecord> {
        return when (this) {
            DATE_NEWEST_FIRST -> records.sortedByDescending { it.timestamp }
            DATE_OLDEST_FIRST -> records.sortedBy { it.timestamp }
            TEMPERATURE_HIGHEST_FIRST ->
                records.sortedByDescending {
                    // Convert to Celsius for consistent comparison
                    when (it.unit) {
                        TemperatureUnit.CELSIUS -> it.temperature
                        TemperatureUnit.FAHRENHEIT -> (it.temperature - 32) * 5 / 9
                    }
                }
            TEMPERATURE_LOWEST_FIRST ->
                records.sortedBy {
                    // Convert to Celsius for consistent comparison
                    when (it.unit) {
                        TemperatureUnit.CELSIUS -> it.temperature
                        TemperatureUnit.FAHRENHEIT -> (it.temperature - 32) * 5 / 9
                    }
                }
        }
    }
}
