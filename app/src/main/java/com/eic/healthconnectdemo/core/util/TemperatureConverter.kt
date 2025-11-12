package com.eic.healthconnectdemo.core.util

import com.eic.healthconnectdemo.domain.model.TemperatureUnit

/**
 * Centralized temperature conversion utility.
 * Follows Single Responsibility Principle - handles only temperature conversions.
 */
object TemperatureConverter {
    /**
     * Convert temperature from one unit to another.
     *
     * @param value The temperature value to convert
     * @param from Source unit
     * @param to Target unit
     * @return Converted temperature value
     */
    fun convert(
        value: Double,
        from: TemperatureUnit,
        to: TemperatureUnit,
    ): Double {
        if (from == to) return value

        return when {
            from == TemperatureUnit.CELSIUS && to == TemperatureUnit.FAHRENHEIT ->
                celsiusToFahrenheit(value)
            from == TemperatureUnit.FAHRENHEIT && to == TemperatureUnit.CELSIUS ->
                fahrenheitToCelsius(value)
            else -> value
        }
    }

    /**
     * Convert Celsius to Fahrenheit.
     * Formula: F = (C × 9/5) + 32
     */
    @Suppress("MagicNumber")
    fun celsiusToFahrenheit(celsius: Double): Double = (celsius * 9.0 / 5.0) + 32.0

    /**
     * Convert Fahrenheit to Celsius.
     * Formula: C = (F - 32) × 5/9
     */
    @Suppress("MagicNumber")
    fun fahrenheitToCelsius(fahrenheit: Double): Double = (fahrenheit - 32.0) * 5.0 / 9.0

    /**
     * Ensure temperature is in Celsius, converting if necessary.
     */
    fun toCelsius(
        value: Double,
        unit: TemperatureUnit,
    ): Double = convert(value, unit, TemperatureUnit.CELSIUS)

    /**
     * Ensure temperature is in Fahrenheit, converting if necessary.
     */
    fun toFahrenheit(
        value: Double,
        unit: TemperatureUnit,
    ): Double = convert(value, unit, TemperatureUnit.FAHRENHEIT)
}
