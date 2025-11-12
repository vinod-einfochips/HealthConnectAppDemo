package com.eic.healthconnectdemo.domain.usecase

import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import com.eic.healthconnectdemo.domain.repository.HealthConnectRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.days

/**
 * Unit tests for RecordTemperatureUseCase.
 * Tests business logic validation and repository interaction.
 */
class RecordTemperatureUseCaseTest {
    private lateinit var repository: HealthConnectRepository
    private lateinit var useCase: RecordTemperatureUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = RecordTemperatureUseCase(repository)
    }

    @Test
    fun `invoke with valid celsius temperature succeeds`() =
        runTest {
            // Given
            val temperature = 37.0
            val unit = TemperatureUnit.CELSIUS
            coEvery { repository.recordTemperature(any()) } returns Result.Success(Unit)

            // When
            val result = useCase(temperature, unit)

            // Then
            assertTrue(result is Result.Success)
            coVerify { repository.recordTemperature(any()) }
        }

    @Test
    fun `invoke with valid fahrenheit temperature succeeds`() =
        runTest {
            // Given
            val temperature = 98.6
            val unit = TemperatureUnit.FAHRENHEIT
            coEvery { repository.recordTemperature(any()) } returns Result.Success(Unit)

            // When
            val result = useCase(temperature, unit)

            // Then
            assertTrue(result is Result.Success)
            coVerify { repository.recordTemperature(any()) }
        }

    @Test
    fun `invoke with temperature below valid range fails`() =
        runTest {
            // Given
            val temperature = 30.0 // Below valid range for Celsius
            val unit = TemperatureUnit.CELSIUS

            // When
            val result = useCase(temperature, unit)

            // Then
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).exception
            assertTrue(error.message?.contains("Temperature must be between") == true)
        }

    @Test
    fun `invoke with temperature above valid range fails`() =
        runTest {
            // Given
            val temperature = 45.0 // Above valid range for Celsius
            val unit = TemperatureUnit.CELSIUS

            // When
            val result = useCase(temperature, unit)

            // Then
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).exception
            assertTrue(error.message?.contains("Temperature must be between") == true)
        }

    @Test
    fun `invoke with future timestamp fails`() =
        runTest {
            // Given
            val temperature = 37.0
            val unit = TemperatureUnit.CELSIUS
            val futureTimestamp = Clock.System.now().plus(1.days)

            // When
            val result = useCase(temperature, unit, futureTimestamp)

            // Then
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).exception
            assertTrue(error.message?.contains("cannot be in the future") == true)
        }

    @Test
    fun `invoke propagates repository errors`() =
        runTest {
            // Given
            val temperature = 37.0
            val unit = TemperatureUnit.CELSIUS
            val exception = Exception("Network error")
            coEvery { repository.recordTemperature(any()) } returns Result.Error(exception)

            // When
            val result = useCase(temperature, unit)

            // Then
            assertTrue(result is Result.Error)
            assertEquals(exception, (result as Result.Error).exception)
        }

    @Test
    fun `invoke creates correct temperature record`() =
        runTest {
            // Given
            val temperature = 37.5
            val unit = TemperatureUnit.CELSIUS
            val timestamp = Clock.System.now()
            var capturedRecord: TemperatureRecord? = null

            coEvery { repository.recordTemperature(any()) } answers {
                capturedRecord = firstArg()
                Result.Success(Unit)
            }

            // When
            useCase(temperature, unit, timestamp)

            // Then
            assertEquals(temperature, capturedRecord?.temperature)
            assertEquals(unit, capturedRecord?.unit)
            assertEquals(timestamp, capturedRecord?.timestamp)
        }
}
