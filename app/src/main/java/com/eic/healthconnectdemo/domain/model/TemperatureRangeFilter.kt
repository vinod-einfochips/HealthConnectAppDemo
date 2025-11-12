package com.eic.healthconnectdemo.domain.model

/**
 * Enum representing temperature range filter options.
 * Ranges are defined in Celsius for consistency.
 */
enum class TemperatureRangeFilter {
    ALL,
    LOW, // Below 36.5°C (97.7°F) - Hypothermia range
    MEDIUM, // 36.5°C to 37.5°C (97.7°F to 99.5°F) - Normal range
    HIGH, // Above 37.5°C (99.5°F) - Fever range
    ;

    /**
     * Returns the display name for this filter.
     */
    fun getDisplayName(): String {
        return when (this) {
            ALL -> "All Temperatures"
            LOW -> "Low (< 36.5°C)"
            MEDIUM -> "Normal (36.5-37.5°C)"
            HIGH -> "High (> 37.5°C)"
        }
    }

    /**
     * Returns the temperature range in Celsius.
     */
    fun getRangeCelsius(): ClosedFloatingPointRange<Double>? {
        return when (this) {
            ALL -> null
            LOW -> 35.0..36.49
            MEDIUM -> 36.5..37.5
            HIGH -> 37.51..42.0
        }
    }

    /**
     * Checks if a given temperature (in Celsius) falls within this filter range.
     */
    fun matchesCelsius(temperatureCelsius: Double): Boolean {
        val range = getRangeCelsius() ?: return true
        return temperatureCelsius in range
    }

    /**
     * Checks if a temperature record matches this filter.
     * Automatically converts Fahrenheit to Celsius if needed.
     */
    fun matches(record: TemperatureRecord): Boolean {
        val tempInCelsius =
            when (record.unit) {
                TemperatureUnit.CELSIUS -> record.temperature
                TemperatureUnit.FAHRENHEIT -> (record.temperature - 32) * 5 / 9
            }
        return matchesCelsius(tempInCelsius)
    }
}
