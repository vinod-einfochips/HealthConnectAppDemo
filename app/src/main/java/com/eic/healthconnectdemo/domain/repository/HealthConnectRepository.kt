package com.eic.healthconnectdemo.domain.repository

import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.TemperatureRecord

/**
 * Repository interface for Health Connect operations.
 * Defines the contract for data operations without exposing implementation details.
 */
interface HealthConnectRepository {
    /**
     * Records a temperature measurement to Health Connect.
     *
     * @param record The temperature record to save
     * @return Result.Success if recording succeeds, Result.Error if it fails
     */
    suspend fun recordTemperature(record: TemperatureRecord): Result<Unit>

    /**
     * Checks if Health Connect is available on the device.
     *
     * @return Result.Success(true) if available, Result.Success(false) if not available
     */
    suspend fun checkAvailability(): Result<Boolean>

    /**
     * Checks if the specified permissions are granted.
     *
     * @param permissions Set of permission strings to check
     * @return Result.Success(true) if all permissions granted, Result.Success(false) otherwise
     */
    suspend fun checkPermissions(permissions: Set<String>): Result<Boolean>
}
