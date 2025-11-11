package com.eic.healthconnectdemo.util

/**
 * Application-wide constants.
 */
object Constants {
    /**
     * Temperature validation ranges
     */
    object TemperatureRanges {
        const val CELSIUS_MIN = 35.0
        const val CELSIUS_MAX = 42.0
        const val FAHRENHEIT_MIN = 95.0
        const val FAHRENHEIT_MAX = 107.6
    }

    /**
     * UI constants
     */
    object UI {
        const val SUCCESS_MESSAGE_DURATION_MS = 3000L
    }

    /**
     * Health Connect package name
     */
    const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"
}
