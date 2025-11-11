package com.eic.healthconnectdemo.data.mapper

import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.units.Temperature
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import java.time.Instant
import java.time.ZoneOffset

/**
 * Extension function to convert domain TemperatureRecord to Health Connect BodyTemperatureRecord.
 * Health Connect stores temperature in Celsius, so conversion is performed if needed.
 */
fun TemperatureRecord.toHealthConnectRecord(): BodyTemperatureRecord {
    // Convert to Celsius if needed (Health Connect stores in Celsius)
    val celsiusValue = when (unit) {
        TemperatureUnit.CELSIUS -> temperature
        TemperatureUnit.FAHRENHEIT -> (temperature - 32) * 5 / 9
    }

    return BodyTemperatureRecord(
        time = Instant.ofEpochMilli(timestamp.toEpochMilliseconds()),
        zoneOffset = ZoneOffset.UTC,
        temperature = Temperature.celsius(celsiusValue)
    )
}

/**
 * Extension function to convert Health Connect BodyTemperatureRecord to domain TemperatureRecord.
 * Returns temperature in Celsius by default.
 */
fun BodyTemperatureRecord.toDomainModel(): TemperatureRecord {
    return TemperatureRecord(
        recordId = metadata.id,
        temperature = temperature.inCelsius,
        unit = TemperatureUnit.CELSIUS,
        timestamp = kotlinx.datetime.Instant.fromEpochMilliseconds(time.toEpochMilli()),
        measurementLocation = null
    )
}
