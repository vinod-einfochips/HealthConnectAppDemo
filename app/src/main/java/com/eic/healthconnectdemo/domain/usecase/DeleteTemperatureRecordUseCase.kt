package com.eic.healthconnectdemo.domain.usecase

import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.repository.HealthConnectRepository
import javax.inject.Inject

/**
 * Use case for deleting a temperature record from Health Connect.
 */
class DeleteTemperatureRecordUseCase
    @Inject
    constructor(
        private val repository: HealthConnectRepository,
    ) {
        /**
         * Deletes a temperature record.
         *
         * @param recordId The ID of the record to delete
         * @return Result.Success if deletion succeeds, Result.Error if it fails
         */
        suspend operator fun invoke(recordId: String): Result<Unit> {
            return try {
                require(recordId.isNotBlank()) {
                    "Record ID cannot be blank"
                }
                repository.deleteTemperatureRecord(recordId)
            } catch (e: IllegalArgumentException) {
                Result.Error(e as Exception)
            } catch (e: Exception) {
                Result.Error(e)
            }
        }
    }
