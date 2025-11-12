package com.eic.healthconnectdemo.core.util

import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for TemperatureConverter.
 * Tests temperature conversion logic for accuracy and edge cases.
 */
class TemperatureConverterTest {
    private val delta = 0.01 // Acceptable delta for floating point comparisons

    @Test
    fun `celsiusToFahrenheit converts correctly`() {
        assertEquals(32.0, TemperatureConverter.celsiusToFahrenheit(0.0), delta)
        assertEquals(212.0, TemperatureConverter.celsiusToFahrenheit(100.0), delta)
        assertEquals(98.6, TemperatureConverter.celsiusToFahrenheit(37.0), delta)
    }

    @Test
    fun `fahrenheitToCelsius converts correctly`() {
        assertEquals(0.0, TemperatureConverter.fahrenheitToCelsius(32.0), delta)
        assertEquals(100.0, TemperatureConverter.fahrenheitToCelsius(212.0), delta)
        assertEquals(37.0, TemperatureConverter.fahrenheitToCelsius(98.6), delta)
    }

    @Test
    fun `convert returns same value when units are identical`() {
        val value = 37.5
        assertEquals(
            value,
            TemperatureConverter.convert(value, TemperatureUnit.CELSIUS, TemperatureUnit.CELSIUS),
            delta,
        )
        assertEquals(
            value,
            TemperatureConverter.convert(value, TemperatureUnit.FAHRENHEIT, TemperatureUnit.FAHRENHEIT),
            delta,
        )
    }

    @Test
    fun `convert celsius to fahrenheit`() {
        assertEquals(
            98.6,
            TemperatureConverter.convert(37.0, TemperatureUnit.CELSIUS, TemperatureUnit.FAHRENHEIT),
            delta,
        )
    }

    @Test
    fun `convert fahrenheit to celsius`() {
        assertEquals(
            37.0,
            TemperatureConverter.convert(98.6, TemperatureUnit.FAHRENHEIT, TemperatureUnit.CELSIUS),
            delta,
        )
    }

    @Test
    fun `toCelsius converts correctly from fahrenheit`() {
        assertEquals(37.0, TemperatureConverter.toCelsius(98.6, TemperatureUnit.FAHRENHEIT), delta)
    }

    @Test
    fun `toCelsius returns same value for celsius`() {
        assertEquals(37.0, TemperatureConverter.toCelsius(37.0, TemperatureUnit.CELSIUS), delta)
    }

    @Test
    fun `toFahrenheit converts correctly from celsius`() {
        assertEquals(98.6, TemperatureConverter.toFahrenheit(37.0, TemperatureUnit.CELSIUS), delta)
    }

    @Test
    fun `toFahrenheit returns same value for fahrenheit`() {
        assertEquals(98.6, TemperatureConverter.toFahrenheit(98.6, TemperatureUnit.FAHRENHEIT), delta)
    }

    @Test
    fun `conversion handles negative temperatures`() {
        assertEquals(-40.0, TemperatureConverter.celsiusToFahrenheit(-40.0), delta)
        assertEquals(-40.0, TemperatureConverter.fahrenheitToCelsius(-40.0), delta)
    }

    @Test
    fun `conversion handles extreme temperatures`() {
        assertEquals(1832.0, TemperatureConverter.celsiusToFahrenheit(1000.0), delta)
        assertEquals(537.78, TemperatureConverter.fahrenheitToCelsius(1000.0), delta)
    }
}
