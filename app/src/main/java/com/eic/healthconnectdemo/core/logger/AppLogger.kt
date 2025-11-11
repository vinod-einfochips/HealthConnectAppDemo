package com.eic.healthconnectdemo.core.logger

import android.util.Log
import com.eic.healthconnectdemo.core.config.AppConfig

/**
 * Centralized logging utility that respects environment configuration.
 */
object AppLogger {
    
    private const val DEFAULT_TAG = "HealthConnect"
    
    /**
     * Log debug message.
     */
    fun d(tag: String = DEFAULT_TAG, message: String) {
        if (AppConfig.isLoggingEnabled) {
            Log.d(tag, message)
        }
    }
    
    /**
     * Log info message.
     */
    fun i(tag: String = DEFAULT_TAG, message: String) {
        if (AppConfig.isLoggingEnabled) {
            Log.i(tag, message)
        }
    }
    
    /**
     * Log warning message.
     */
    fun w(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (AppConfig.isLoggingEnabled) {
            if (throwable != null) {
                Log.w(tag, message, throwable)
            } else {
                Log.w(tag, message)
            }
        }
    }
    
    /**
     * Log error message.
     */
    fun e(tag: String = DEFAULT_TAG, message: String, throwable: Throwable? = null) {
        if (AppConfig.isLoggingEnabled) {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        }
    }
    
    /**
     * Log verbose message.
     */
    fun v(tag: String = DEFAULT_TAG, message: String) {
        if (AppConfig.isLoggingEnabled) {
            Log.v(tag, message)
        }
    }
}
