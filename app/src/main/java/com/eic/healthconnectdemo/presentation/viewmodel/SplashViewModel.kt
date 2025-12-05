package com.eic.healthconnectdemo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eic.healthconnectdemo.core.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing splash screen state and navigation logic.
 */
@HiltViewModel
class SplashViewModel
    @Inject
    constructor() : ViewModel() {
        private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
        val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()

        /**
         * Handles the "Get Started" button click.
         * Navigates to the main screen after a brief delay for smooth transition.
         */
        fun onGetStartedClicked() {
            viewModelScope.launch {
                delay(Constants.UI.SPLASH_BUTTON_DELAY_MS) // Brief delay for button animation
                _navigationEvent.value = NavigationEvent.NavigateToMain
            }
        }

        /**
         * Resets the navigation event after it has been consumed.
         */
        fun onNavigationEventConsumed() {
            _navigationEvent.value = null
        }

        /**
         * Sealed class representing navigation events from splash screen.
         */
        sealed class NavigationEvent {
            data object NavigateToMain : NavigationEvent()
        }
    }
