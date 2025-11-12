package com.eic.healthconnectdemo

import android.app.Application
import com.eic.healthconnectdemo.core.config.AppConfig
import com.eic.healthconnectdemo.core.logger.AppLogger
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Health Connect Demo.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class HealthConnectApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeApp()
    }

    private fun initializeApp() {
        AppLogger.i("HealthConnectApp", "Initializing application")
        AppLogger.i("HealthConnectApp", "Environment: ${AppConfig.environment}")
        AppLogger.i("HealthConnectApp", "Version: ${AppConfig.versionName} (${AppConfig.versionCode})")
        AppLogger.i("HealthConnectApp", "Debug mode: ${AppConfig.isDebug}")
        AppLogger.i("HealthConnectApp", "Logging enabled: ${AppConfig.isLoggingEnabled}")
    }
}
