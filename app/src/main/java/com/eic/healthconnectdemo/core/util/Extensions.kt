package com.eic.healthconnectdemo.core.util

import android.content.Context
import android.widget.Toast
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Extension functions for common operations.
 */

/**
 * Show a short toast message.
 */
fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * Show a long toast message.
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

/**
 * Format Instant to date string.
 */
fun Instant.toDateString(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val formatter = DateTimeFormatter.ofPattern(Constants.DateTime.DATE_FORMAT)
    return localDateTime.toJavaLocalDateTime().format(formatter)
}

/**
 * Format Instant to time string.
 */
fun Instant.toTimeString(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val formatter = DateTimeFormatter.ofPattern(Constants.DateTime.TIME_FORMAT)
    return localDateTime.toJavaLocalDateTime().format(formatter)
}

/**
 * Format Instant to date and time string.
 */
fun Instant.toDateTimeString(): String {
    val localDateTime = this.toLocalDateTime(TimeZone.currentSystemDefault())
    val formatter = DateTimeFormatter.ofPattern(Constants.DateTime.DATE_TIME_FORMAT)
    return localDateTime.toJavaLocalDateTime().format(formatter)
}

/**
 * Convert Celsius to Fahrenheit.
 */
fun Double.celsiusToFahrenheit(): Double {
    return (this * 9 / 5) + 32
}

/**
 * Convert Fahrenheit to Celsius.
 */
fun Double.fahrenheitToCelsius(): Double {
    return (this - 32) * 5 / 9
}

/**
 * Format temperature value with unit.
 */
fun Double.formatTemperature(unit: String): String {
    return String.format("%.1f°%s", this, unit)
}
