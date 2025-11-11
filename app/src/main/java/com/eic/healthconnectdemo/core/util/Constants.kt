package com.eic.healthconnectdemo.core.util

/**
 * Application-wide constants.
 */
object Constants {
    
    // Health Connect
    object HealthConnect {
        const val PACKAGE_NAME = "com.google.android.apps.healthdata"
        const val PLAY_STORE_URL = "market://details?id=$PACKAGE_NAME"
        const val PLAY_STORE_WEB_URL = "https://play.google.com/store/apps/details?id=$PACKAGE_NAME"
    }
    
    // Temperature
    object Temperature {
        const val MIN_CELSIUS = 30.0
        const val MAX_CELSIUS = 45.0
        const val MIN_FAHRENHEIT = 86.0
        const val MAX_FAHRENHEIT = 113.0
        
        // Temperature status thresholds (in Celsius)
        const val LOW_THRESHOLD = 36.0
        const val NORMAL_THRESHOLD = 37.5
        const val ELEVATED_THRESHOLD = 38.0
        const val HIGH_THRESHOLD = 39.0
    }
    
    // Date & Time
    object DateTime {
        const val DATE_FORMAT = "MMM dd, yyyy"
        const val TIME_FORMAT = "hh:mm a"
        const val DATE_TIME_FORMAT = "MMM dd, yyyy hh:mm a"
    }
    
    // UI
    object UI {
        const val DEBOUNCE_DELAY_MS = 300L
        const val ANIMATION_DURATION_MS = 300L
    }
}
