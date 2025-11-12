package com.eic.healthconnectdemo.core.config

/**
 * Enum representing different application environments.
 */
enum class Environment {
    DEV,
    QA,
    PRODUCTION,
    ;

    companion object {
        fun fromString(value: String): Environment {
            return when (value.uppercase()) {
                "DEV" -> DEV
                "QA" -> QA
                "PRODUCTION" -> PRODUCTION
                else -> DEV
            }
        }
    }
}
