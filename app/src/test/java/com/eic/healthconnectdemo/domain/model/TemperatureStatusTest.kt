package com.eic.healthconnectdemo.domain.model

import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for TemperatureStatus.
 * Tests temperature status classification logic.
 */
class TemperatureStatusTest {
    @Test
    fun `fromCelsius returns LOW for temperatures below 36_1`() {
        assertEquals(TemperatureStatus.LOW, TemperatureStatus.fromCelsius(35.0))
        assertEquals(TemperatureStatus.LOW, TemperatureStatus.fromCelsius(36.0))
    }

    @Test
    fun `fromCelsius returns NORMAL for temperatures between 36_1 and 37_2`() {
        assertEquals(TemperatureStatus.NORMAL, TemperatureStatus.fromCelsius(36.5))
        assertEquals(TemperatureStatus.NORMAL, TemperatureStatus.fromCelsius(37.0))
        assertEquals(TemperatureStatus.NORMAL, TemperatureStatus.fromCelsius(37.2))
    }

    @Test
    fun `fromCelsius returns ELEVATED for temperatures between 37_3 and 38_0`() {
        assertEquals(TemperatureStatus.ELEVATED, TemperatureStatus.fromCelsius(37.5))
        assertEquals(TemperatureStatus.ELEVATED, TemperatureStatus.fromCelsius(38.0))
    }

    @Test
    fun `fromCelsius returns HIGH for temperatures above 38_0`() {
        assertEquals(TemperatureStatus.HIGH, TemperatureStatus.fromCelsius(38.5))
        assertEquals(TemperatureStatus.HIGH, TemperatureStatus.fromCelsius(40.0))
    }

    @Test
    fun `fromRecord returns correct status for celsius record`() {
        val record =
            TemperatureRecord(
                temperature = 37.0,
                unit = TemperatureUnit.CELSIUS,
                timestamp = Clock.System.now(),
            )
        assertEquals(TemperatureStatus.NORMAL, TemperatureStatus.fromRecord(record))
    }

    @Test
    fun `fromRecord returns correct status for fahrenheit record`() {
        // ~37°C
        val record =
            TemperatureRecord(
                temperature = 98.6,
                unit = TemperatureUnit.FAHRENHEIT,
                timestamp = Clock.System.now(),
            )
        assertEquals(TemperatureStatus.NORMAL, TemperatureStatus.fromRecord(record))
    }

    @Test
    fun `status enum has correct display names`() {
        assertEquals("Low", TemperatureStatus.LOW.displayName)
        assertEquals("Normal", TemperatureStatus.NORMAL.displayName)
        assertEquals("Elevated", TemperatureStatus.ELEVATED.displayName)
        assertEquals("High", TemperatureStatus.HIGH.displayName)
    }

    @Test
    fun `status enum has color resources`() {
        assertEquals(android.R.color.holo_blue_light, TemperatureStatus.LOW.colorRes)
        assertEquals(android.R.color.holo_green_light, TemperatureStatus.NORMAL.colorRes)
        assertEquals(android.R.color.holo_orange_light, TemperatureStatus.ELEVATED.colorRes)
        assertEquals(android.R.color.holo_red_light, TemperatureStatus.HIGH.colorRes)
    }
}
