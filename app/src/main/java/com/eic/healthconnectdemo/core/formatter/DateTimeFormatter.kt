package com.eic.healthconnectdemo.core.formatter

import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Centralized date/time formatting utility.
 * Follows Single Responsibility Principle - handles only date/time formatting.
 * Thread-safe singleton with reusable formatters.
 */
object DateTimeFormatter {
    // Thread-safe formatters using ThreadLocal to avoid synchronization overhead
    private val dateTimeFormat =
        ThreadLocal.withInitial {
            SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        }

    private val dateFormat =
        ThreadLocal.withInitial {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        }

    private val timeFormat =
        ThreadLocal.withInitial {
            SimpleDateFormat("hh:mm a", Locale.getDefault())
        }

    /**
     * Format an Instant to a full date-time string.
     *
     * @param instant The instant to format
     * @return Formatted string (e.g., "Jan 15, 2024 at 03:45 PM")
     */
    fun formatDateTime(instant: Instant): String {
        val date = Date(instant.toEpochMilliseconds())
        return dateTimeFormat.get()?.format(date) ?: instant.toString()
    }

    /**
     * Format an Instant to a date string.
     *
     * @param instant The instant to format
     * @return Formatted string (e.g., "Jan 15, 2024")
     */
    fun formatDate(instant: Instant): String {
        val date = Date(instant.toEpochMilliseconds())
        return dateFormat.get()?.format(date) ?: instant.toString()
    }

    /**
     * Format an Instant to a time string.
     *
     * @param instant The instant to format
     * @return Formatted string (e.g., "03:45 PM")
     */
    fun formatTime(instant: Instant): String {
        val date = Date(instant.toEpochMilliseconds())
        return timeFormat.get()?.format(date) ?: instant.toString()
    }
}
