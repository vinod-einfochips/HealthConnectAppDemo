package com.eic.healthconnectdemo.domain.usecase

import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.repository.HealthConnectRepository
import javax.inject.Inject

/**
 * Use case for checking Health Connect availability on the device.
 */
class CheckHealthConnectAvailabilityUseCase
    @Inject
    constructor(
        private val repository: HealthConnectRepository,
    ) {
        /**
         * Checks if Health Connect is available.
         *
         * @return Result.Success(true) if available, Result.Success(false) if not
         */
        suspend operator fun invoke(): Result<Boolean> {
            return repository.checkAvailability()
        }
    }
