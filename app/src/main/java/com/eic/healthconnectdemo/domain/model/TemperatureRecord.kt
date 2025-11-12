package com.eic.healthconnectdemo.domain.model

import kotlinx.datetime.Instant

/**
 * Domain model representing a body temperature measurement.
 *
 * @property recordId Unique identifier for the record (from Health Connect)
 * @property temperature The temperature value
 * @property unit The unit of measurement (Celsius or Fahrenheit)
 * @property timestamp The time when the measurement was taken
 * @property measurementLocation Optional location where temperature was measured (e.g., "oral", "armpit")
 */
data class TemperatureRecord(
    val recordId: String? = null,
    val temperature: Double,
    val unit: TemperatureUnit,
    val timestamp: Instant,
    val measurementLocation: String? = null,
) {
    init {
        require(temperature in unit.getValidRange()) {
            "Temperature $temperature is outside valid range ${unit.getValidRange()} for ${unit.name}"
        }
    }
}
