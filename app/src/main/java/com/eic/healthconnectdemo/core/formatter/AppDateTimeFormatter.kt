package com.eic.healthconnectdemo.core.formatter

import kotlinx.datetime.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Centralized date/time formatting utility.
 * Follows Single Responsibility Principle - handles only date/time formatting.
 * Thread-safe singleton with reusable formatters using modern java.time API.
 */
object AppDateTimeFormatter {
    // Modern thread-safe formatters using java.time.format.DateTimeFormatter
    private val dateTimeFormat: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

    private val dateFormat: DateTimeFormatter =
        DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

    private val timeFormat: DateTimeFormatter =
        DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())

    private val systemZone: ZoneId = ZoneId.systemDefault()

    /**
     * Format an Instant to a full date-time string.
     *
     * @param instant The instant to format
     * @return Formatted string (e.g., "Jan 15, 2024 at 03:45 PM")
     */
    fun formatDateTime(instant: Instant): String {
        val javaInstant = java.time.Instant.ofEpochMilli(instant.toEpochMilliseconds())
        val zonedDateTime = javaInstant.atZone(systemZone)
        return dateTimeFormat.format(zonedDateTime)
    }

    /**
     * Format an Instant to a date string.
     *
     * @param instant The instant to format
     * @return Formatted string (e.g., "Jan 15, 2024")
     */
    fun formatDate(instant: Instant): String {
        val javaInstant = java.time.Instant.ofEpochMilli(instant.toEpochMilliseconds())
        val zonedDateTime = javaInstant.atZone(systemZone)
        return dateFormat.format(zonedDateTime)
    }

    /**
     * Format an Instant to a time string.
     *
     * @param instant The instant to format
     * @return Formatted string (e.g., "03:45 PM")
     */
    fun formatTime(instant: Instant): String {
        val javaInstant = java.time.Instant.ofEpochMilli(instant.toEpochMilliseconds())
        val zonedDateTime = javaInstant.atZone(systemZone)
        return timeFormat.format(zonedDateTime)
    }
}
