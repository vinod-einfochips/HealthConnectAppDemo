package com.eic.healthconnectdemo.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eic.healthconnectdemo.core.logger.AppLogger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Base ViewModel with common functionality.
 */
abstract class BaseViewModel : ViewModel() {
    private val tag = this::class.java.simpleName

    /**
     * Exception handler for coroutines.
     */
    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            AppLogger.e(tag, "Coroutine exception", throwable)
            handleError(throwable)
        }

    /**
     * Launch a coroutine with exception handling.
     */
    protected fun launchSafe(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    /**
     * Handle errors. Override in child classes for custom error handling.
     */
    protected open fun handleError(throwable: Throwable) {
        AppLogger.e(tag, "Error occurred: ${throwable.message}", throwable)
    }

    /**
     * Log debug message.
     */
    protected fun logDebug(message: String) {
        AppLogger.d(tag, message)
    }

    /**
     * Log info message.
     */
    protected fun logInfo(message: String) {
        AppLogger.i(tag, message)
    }

    /**
     * Log error message.
     */
    protected fun logError(
        message: String,
        throwable: Throwable? = null,
    ) {
        AppLogger.e(tag, message, throwable)
    }
}
