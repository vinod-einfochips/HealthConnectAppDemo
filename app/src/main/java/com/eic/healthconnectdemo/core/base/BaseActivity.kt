package com.eic.healthconnectdemo.core.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.eic.healthconnectdemo.core.logger.AppLogger

/**
 * Base Activity with common functionality.
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    private val tag = this::class.java.simpleName

    /**
     * Inflate the view binding.
     */
    abstract fun getViewBinding(): VB

    /**
     * Setup views and observers.
     */
    abstract fun setupViews()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root)

        logDebug("onCreate")
        setupViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        logDebug("onDestroy")
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
