package com.eic.healthconnectdemo.domain.model

import androidx.annotation.ColorRes

/**
 * Represents the status of a temperature reading.
 * Follows Open/Closed Principle - extensible without modification.
 */
enum class TemperatureStatus(
    val displayName: String,
    @ColorRes val colorRes: Int,
    val rangeInCelsius: ClosedFloatingPointRange<Double>,
) {
    LOW(
        displayName = "Low",
        colorRes = android.R.color.holo_blue_light,
        rangeInCelsius = 0.0..36.0,
    ),
    NORMAL(
        displayName = "Normal",
        colorRes = android.R.color.holo_green_light,
        rangeInCelsius = 36.1..37.2,
    ),
    ELEVATED(
        displayName = "Elevated",
        colorRes = android.R.color.holo_orange_light,
        rangeInCelsius = 37.3..38.0,
    ),
    HIGH(
        displayName = "High",
        colorRes = android.R.color.holo_red_light,
        rangeInCelsius = 38.1..Double.MAX_VALUE,
    ),
    ;

    companion object {
        /**
         * Determine temperature status from a Celsius value.
         *
         * @param celsiusTemp Temperature in Celsius
         * @return Corresponding TemperatureStatus
         */
        fun fromCelsius(celsiusTemp: Double): TemperatureStatus {
            return entries.firstOrNull { celsiusTemp in it.rangeInCelsius } ?: HIGH
        }

        /**
         * Determine temperature status from a temperature record.
         *
         * @param record Temperature record
         * @return Corresponding TemperatureStatus
         */
        @Suppress("MagicNumber")
        fun fromRecord(record: TemperatureRecord): TemperatureStatus {
            val celsiusTemp =
                when (record.unit) {
                    TemperatureUnit.CELSIUS -> record.temperature
                    TemperatureUnit.FAHRENHEIT -> (record.temperature - 32) * 5 / 9
                }
            return fromCelsius(celsiusTemp)
        }
    }
}
