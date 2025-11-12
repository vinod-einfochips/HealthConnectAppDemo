package com.eic.healthconnectdemo.presentation.viewmodel

import app.cash.turbine.test
import com.eic.healthconnectdemo.domain.model.Result
import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import com.eic.healthconnectdemo.domain.usecase.CheckHealthConnectAvailabilityUseCase
import com.eic.healthconnectdemo.domain.usecase.RecordTemperatureUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for TemperatureViewModel.
 * Tests UI state management and business logic coordination.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TemperatureViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var recordTemperatureUseCase: RecordTemperatureUseCase
    private lateinit var checkHealthConnectAvailabilityUseCase: CheckHealthConnectAvailabilityUseCase
    private lateinit var viewModel: TemperatureViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        recordTemperatureUseCase = mockk()
        checkHealthConnectAvailabilityUseCase = mockk()

        // Default mock behavior
        coEvery { checkHealthConnectAvailabilityUseCase() } returns Result.Success(true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is correct`() =
        runTest {
            // Given
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()

            // When/Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertFalse(state.isLoading)
                assertFalse(state.isSuccess)
                assertNull(state.error)
                assertEquals("", state.temperatureValue)
                assertEquals(TemperatureUnit.CELSIUS, state.selectedUnit)
            }
        }

    @Test
    fun `recordTemperature with valid input succeeds`() =
        runTest {
            // Given
            coEvery { recordTemperatureUseCase(any(), any(), any()) } returns Result.Success(Unit)
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()

            // When
            viewModel.recordTemperature(37.0, TemperatureUnit.CELSIUS)
            advanceUntilIdle()

            // Then - Wait for success state after delay
            testScheduler.advanceTimeBy(3100) // Success message auto-dismisses after 3 seconds
            val state = viewModel.uiState.value
            assertFalse(state.isLoading)
            assertFalse(state.isSuccess) // Should be false after auto-dismiss
            assertNull(state.error)
            coVerify { recordTemperatureUseCase(37.0, TemperatureUnit.CELSIUS, any()) }
        }

    @Test
    fun `recordTemperature with invalid temperature shows error`() =
        runTest {
            // Given
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()

            // When
            viewModel.recordTemperature(30.0, TemperatureUnit.CELSIUS) // Below valid range
            advanceUntilIdle()

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertFalse(state.isLoading)
                assertFalse(state.isSuccess)
                assertTrue(state.error?.contains("Temperature must be between") == true)
            }
        }

    @Test
    fun `recordTemperature with repository error shows error`() =
        runTest {
            // Given
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            val errorMessage = "Network error"
            coEvery { recordTemperatureUseCase(any(), any(), any()) } returns
                Result.Error(Exception(errorMessage))
            advanceUntilIdle()

            // When
            viewModel.recordTemperature(37.0, TemperatureUnit.CELSIUS)
            advanceUntilIdle()

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertFalse(state.isLoading)
                assertFalse(state.isSuccess)
                assertEquals(errorMessage, state.error)
            }
        }

    @Test
    fun `updateTemperatureValue updates state`() =
        runTest {
            // Given
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()

            // When
            viewModel.updateTemperatureValue("37.5")

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals("37.5", state.temperatureValue)
            }
        }

    @Test
    fun `updateSelectedUnit updates state`() =
        runTest {
            // Given
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()

            // When
            viewModel.updateSelectedUnit(TemperatureUnit.FAHRENHEIT)

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertEquals(TemperatureUnit.FAHRENHEIT, state.selectedUnit)
            }
        }

    @Test
    fun `clearError clears error state`() =
        runTest {
            // Given
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()
            viewModel.recordTemperature(30.0, TemperatureUnit.CELSIUS) // Trigger error
            advanceUntilIdle()

            // When
            viewModel.clearError()

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertNull(state.error)
            }
        }

    @Test
    fun `setPermissionGranted updates state`() =
        runTest {
            // Given
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()

            // When
            viewModel.setPermissionGranted(true)

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state.permissionGranted)
            }
        }

    @Test
    fun `checkHealthConnectAvailability updates state on success`() =
        runTest {
            // Given
            coEvery { checkHealthConnectAvailabilityUseCase() } returns Result.Success(true)

            // When
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertTrue(state.healthConnectAvailable)
                assertNull(state.error)
            }
        }

    @Test
    fun `checkHealthConnectAvailability shows error when unavailable`() =
        runTest {
            // Given
            coEvery { checkHealthConnectAvailabilityUseCase() } returns Result.Success(false)

            // When
            viewModel = TemperatureViewModel(recordTemperatureUseCase, checkHealthConnectAvailabilityUseCase)
            advanceUntilIdle()

            // Then
            viewModel.uiState.test {
                val state = awaitItem()
                assertFalse(state.healthConnectAvailable)
                assertTrue(state.error?.contains("not available") == true)
            }
        }
}
