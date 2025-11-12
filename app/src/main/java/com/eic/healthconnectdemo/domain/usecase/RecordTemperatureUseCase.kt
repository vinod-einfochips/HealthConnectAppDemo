package com.eic.healthconnectdemo.domain.usecase

import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import com.eic.healthconnectdemo.domain.repository.HealthConnectRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject

/**
 * Use case for recording body temperature to Health Connect.
 * Encapsulates the business logic for temperature recording.
 */
class RecordTemperatureUseCase
    @Inject
    constructor(
        private val repository: HealthConnectRepository,
    ) {
        /**
         * Records a temperature measurement.
         *
         * @param temperature The temperature value
         * @param unit The unit of measurement (Celsius or Fahrenheit)
         * @param timestamp The time of measurement (defaults to current time)
         * @return Result.Success if recording succeeds, Result.Error if it fails
         */
        suspend operator fun invoke(
            temperature: Double,
            unit: TemperatureUnit,
            timestamp: Instant = Clock.System.now(),
        ): Result<Unit> {
            return try {
                // Validate business rules
                require(temperature in unit.getValidRange()) {
                    val range = unit.getValidRange()
                    "Temperature must be between ${range.start} and ${range.endInclusive} ${unit.getSymbol()}"
                }

                require(timestamp <= Clock.System.now()) {
                    "Timestamp cannot be in the future"
                }

                // Create domain model
                val record =
                    TemperatureRecord(
                        temperature = temperature,
                        unit = unit,
                        timestamp = timestamp,
                    )

                // Call repository
                repository.recordTemperature(record)
            } catch (e: IllegalArgumentException) {
                Result.Error(e as Exception)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
