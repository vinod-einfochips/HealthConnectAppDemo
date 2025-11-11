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

    /**
     * Reads temperature records from Health Connect.
     *
     * @param startTime Optional start time for filtering records
     * @param endTime Optional end time for filtering records
     * @return Result.Success with list of temperature records, or Result.Error if reading fails
     */
    suspend fun readTemperatureRecords(
        startTime: kotlinx.datetime.Instant? = null,
        endTime: kotlinx.datetime.Instant? = null
    ): Result<List<TemperatureRecord>>

    /**
     * Deletes a temperature record from Health Connect.
     *
     * @param recordId The ID of the record to delete
     * @return Result.Success if deletion succeeds, Result.Error if it fails
     */
    suspend fun deleteTemperatureRecord(recordId: String): Result<Unit>
}
