package com.eic.healthconnectdemo.data.repository

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.eic.healthconnectdemo.data.mapper.toDomainModel
import com.eic.healthconnectdemo.data.mapper.toHealthConnectRecord
import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
import com.eic.healthconnectdemo.domain.repository.HealthConnectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
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

    override suspend fun readTemperatureRecords(
        startTime: kotlinx.datetime.Instant?,
        endTime: kotlinx.datetime.Instant?
    ): Result<List<TemperatureRecord>> {
        return withContext(Dispatchers.IO) {
            try {
                // Use a large time range if no specific range is provided
                val timeRangeFilter = if (startTime != null && endTime != null) {
                    TimeRangeFilter.between(
                        Instant.ofEpochMilli(startTime.toEpochMilliseconds()),
                        Instant.ofEpochMilli(endTime.toEpochMilliseconds())
                    )
                } else {
                    // Read all records by using a very large time range
                    TimeRangeFilter.between(
                        Instant.EPOCH,
                        Instant.now()
                    )
                }

                val request = ReadRecordsRequest(
                    recordType = BodyTemperatureRecord::class,
                    timeRangeFilter = timeRangeFilter
                )

                val response = healthConnectClient.readRecords(request)
                val records = response.records.map { it.toDomainModel() }
                    .sortedByDescending { it.timestamp }

                Result.Success(records)
            } catch (e: SecurityException) {
                Result.Error(Exception("Permission denied: ${e.message}", e))
            } catch (e: Exception) {
                Result.Error(Exception("Failed to read temperature records: ${e.message}", e))
            }
        }
    }

    override suspend fun deleteTemperatureRecord(recordId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                healthConnectClient.deleteRecords(
                    recordType = BodyTemperatureRecord::class,
                    recordIdsList = listOf(recordId),
                    clientRecordIdsList = emptyList()
                )
                Result.Success(Unit)
            } catch (e: SecurityException) {
                Result.Error(Exception("Permission denied: ${e.message}", e))
            } catch (e: Exception) {
                Result.Error(Exception("Failed to delete temperature record: ${e.message}", e))
            }
        }
    }
}
