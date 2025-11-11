package com.eic.healthconnectdemo.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BodyTemperatureRecord
import com.eic.healthconnectdemo.data.mapper.toHealthConnectRecord
import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
import com.eic.healthconnectdemo.domain.repository.HealthConnectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Implementation of HealthConnectRepository using Health Connect SDK.
 */
class HealthConnectRepositoryImpl @Inject constructor(
    private val healthConnectClient: HealthConnectClient,
    private val context: Context
) : HealthConnectRepository {

    override suspend fun recordTemperature(record: TemperatureRecord): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Map domain model to Health Connect SDK model
                val healthConnectRecord = record.toHealthConnectRecord()

                // Insert record into Health Connect
                healthConnectClient.insertRecords(listOf(healthConnectRecord))

                Result.Success(Unit)
            } catch (e: SecurityException) {
                Result.Error(Exception("Permission denied: ${e.message}", e))
            } catch (e: Exception) {
                Result.Error(Exception("Failed to record temperature: ${e.message}", e))
            }
        }
    }

    override suspend fun checkAvailability(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val status = HealthConnectClient.getSdkStatus(context)
                val isAvailable = status == HealthConnectClient.SDK_AVAILABLE
                Result.Success(isAvailable)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to check availability: ${e.message}", e))
            }
        }
    }

    override suspend fun checkPermissions(permissions: Set<String>): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val grantedPermissions = healthConnectClient.permissionController
                    .getGrantedPermissions()

                // Check if WRITE_BODY_TEMPERATURE permission is granted
                val writePermission = HealthPermission.getWritePermission(BodyTemperatureRecord::class)
                val allGranted = grantedPermissions.contains(writePermission)

                Result.Success(allGranted)
            } catch (e: Exception) {
                Result.Error(Exception("Failed to check permissions: ${e.message}", e))
            }
        }
    }
}
