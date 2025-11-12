package com.eic.healthconnectdemo.domain.model

/**
 * Enum representing temperature measurement units.
 */
enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT,
    ;

    /**
     * Returns the valid temperature range for this unit.
     * - Celsius: 35.0°C to 42.0°C
     * - Fahrenheit: 95.0°F to 107.6°F
     */
    fun getValidRange(): ClosedFloatingPointRange<Double> {
        return when (this) {
            CELSIUS -> 35.0..42.0
            FAHRENHEIT -> 95.0..107.6
        }
    }

    /**
     * Returns the symbol for this unit (°C or °F).
     */
    fun getSymbol(): String {
        return when (this) {
            CELSIUS -> "°C"
            FAHRENHEIT -> "°F"
        }
    }

    /**
     * Returns the display name for this unit.
     */
    fun getDisplayName(): String {
        return when (this) {
            CELSIUS -> "Celsius"
            FAHRENHEIT -> "Fahrenheit"
        }
    }
}
