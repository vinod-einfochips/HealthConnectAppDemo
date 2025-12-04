package com.eic.healthconnectdemo.core.config

import com.eic.healthconnectdemo.BuildConfig

/**
 * Application configuration that provides environment-specific settings.
 */
object AppConfig {
    /**
     * Current environment (DEV, QA, or PRODUCTION).
     */
    val environment: Environment = Environment.fromString(BuildConfig.ENVIRONMENT)

    /**
     * Whether logging is enabled.
     */
    val isLoggingEnabled: Boolean = BuildConfig.ENABLE_LOGGING

    /**
     * Application version name.
     */
    val versionName: String = BuildConfig.VERSION_NAME

    /**
     * Application version code.
     */
    val versionCode: Int = BuildConfig.VERSION_CODE

    /**
     * Whether the app is running in debug mode.
     */
    val isDebug: Boolean = BuildConfig.DEBUG

    /**
     * Application ID.
     */
    val applicationId: String = BuildConfig.APPLICATION_ID

    /**
     * Checks if current environment is development.
     */
    fun isDevelopment(): Boolean = environment == Environment.DEV

    /**
     * Checks if current environment is QA.
     */
    fun isQA(): Boolean = environment == Environment.QA

    /**
     * Checks if current environment is production.
     */
    fun isProduction(): Boolean = environment == Environment.PRODUCTION
}
