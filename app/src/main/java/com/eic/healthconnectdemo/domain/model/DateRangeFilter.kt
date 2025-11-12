package com.eic.healthconnectdemo.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

/**
 * Enum representing date range filter options for temperature history.
 */
enum class DateRangeFilter {
    ALL,
    TODAY,
    LAST_7_DAYS,
    LAST_30_DAYS,
    ;

    /**
     * Returns the display name for this filter.
     */
    fun getDisplayName(): String {
        return when (this) {
            ALL -> "All Time"
            TODAY -> "Today"
            LAST_7_DAYS -> "Last 7 Days"
            LAST_30_DAYS -> "Last 30 Days"
        }
    }

    /**
     * Returns the start instant for this filter range.
     * Returns null for ALL (no filtering).
     */
    fun getStartInstant(): Instant? {
        val now = Clock.System.now()

        return when (this) {
            ALL -> null
            TODAY -> {
                // Start of today (24 hours ago from now)
                now.minus(24.days)
            }
            LAST_7_DAYS -> now.minus(7.days)
            LAST_30_DAYS -> now.minus(30.days)
        }
    }

    /**
     * Checks if a given instant falls within this filter range.
     */
    fun matches(instant: Instant): Boolean {
        val startInstant = getStartInstant() ?: return true
        return instant >= startInstant
    }
}
