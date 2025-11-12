@file:Suppress("Indentation")

package com.eic.healthconnectdemo.core.permission

import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BodyTemperatureRecord
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Centralized permission management for Health Connect.
 * Follows Single Responsibility Principle - handles only permission-related operations.
 * Follows Dependency Inversion Principle - depends on abstractions (HealthConnectClient).
 */
@Singleton
class PermissionManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val healthConnectClient: HealthConnectClient,
    ) {
        companion object {
            /**
             * Required Health Connect permissions for this app.
             */
            val REQUIRED_PERMISSIONS =
                setOf(
                    HealthPermission.getWritePermission(BodyTemperatureRecord::class),
                    HealthPermission.getReadPermission(BodyTemperatureRecord::class),
                )
        }

        /**
         * Check if Health Connect is available on the device.
         *
         * @return SDK status code
         */
        fun getHealthConnectStatus(): Int = HealthConnectClient.getSdkStatus(context)

        /**
         * Check if Health Connect is available.
         */
        fun isHealthConnectAvailable(): Boolean = getHealthConnectStatus() == HealthConnectClient.SDK_AVAILABLE

        /**
         * Check if Health Connect needs an update.
         */
        fun needsHealthConnectUpdate(): Boolean =
            getHealthConnectStatus() == HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED

        /**
         * Check if all required permissions are granted.
         *
         * @return True if all permissions are granted, false otherwise
         */
        @Suppress("TooGenericExceptionCaught", "SwallowedException")
        suspend fun hasAllPermissions(): Boolean {
            return try {
                val granted = healthConnectClient.permissionController.getGrantedPermissions()
                granted.containsAll(REQUIRED_PERMISSIONS)
            } catch (e: Exception) {
                // Return false if permission check fails for any reason
                false
            }
        }

        /**
         * Request Health Connect permissions.
         *
         * @param launcher Activity result launcher for permission request
         */
        fun requestPermissions(launcher: ActivityResultLauncher<Set<String>>) {
            launcher.launch(REQUIRED_PERMISSIONS)
        }

        /**
         * Create a permission request contract.
         */
        fun createPermissionContract() = PermissionController.createRequestPermissionResultContract()
    }
