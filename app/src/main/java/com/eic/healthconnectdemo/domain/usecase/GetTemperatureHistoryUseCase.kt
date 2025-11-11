package com.eic.healthconnectdemo.domain.usecase

import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
import com.eic.healthconnectdemo.domain.repository.HealthConnectRepository
import kotlinx.datetime.Instant
import javax.inject.Inject

/**
 * Use case for retrieving temperature history from Health Connect.
 * Encapsulates the business logic for reading temperature records.
 */
class GetTemperatureHistoryUseCase @Inject constructor(
    private val repository: HealthConnectRepository
) {
    /**
     * Retrieves temperature records from Health Connect.
     *
     * @param startTime Optional start time for filtering records
     * @param endTime Optional end time for filtering records
     * @return Result.Success with list of temperature records, or Result.Error if reading fails
     */
    suspend operator fun invoke(
        startTime: Instant? = null,
        endTime: Instant? = null
    ): Result<List<TemperatureRecord>> {
        return try {
            repository.readTemperatureRecords(startTime, endTime)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
