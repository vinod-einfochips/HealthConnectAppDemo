package com.eic.healthconnectdemo.presentation.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eic.healthconnectdemo.MainActivity
import com.eic.healthconnectdemo.core.util.Constants
import com.eic.healthconnectdemo.databinding.ActivitySplashBinding
import com.eic.healthconnectdemo.presentation.viewmodel.SplashViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Splash screen activity that displays the app branding and provides
 * entry point to the main application.
 * Follows MVVM architecture pattern with view binding.
 */
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        startAnimations()
    }

    /**
     * Sets up UI components and click listeners.
     */
    private fun setupUI() {
        binding.btnGetStarted.setOnClickListener {
            viewModel.onGetStartedClicked()
        }
    }

    /**
     * Observes ViewModel state changes and navigation events.
     */
    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvent.collect { event ->
                    event?.let {
                        handleNavigationEvent(it)
                        viewModel.onNavigationEventConsumed()
                    }
                }
            }
        }
    }

    /**
     * Handles navigation events from the ViewModel.
     */
    private fun handleNavigationEvent(event: SplashViewModel.NavigationEvent) {
        when (event) {
            is SplashViewModel.NavigationEvent.NavigateToMain -> {
                navigateToMain()
            }
        }
    }

    /**
     * Navigates to the main activity and finishes splash screen.
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

        // Add smooth transition animation
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                android.R.anim.fade_in,
                android.R.anim.fade_out,
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    /**
     * Starts entrance animations for splash screen elements.
     */
    private fun startAnimations() {
        // Fade in Google logo
        animateFadeIn(
            binding.googleLogoLayout,
            Constants.UI.SPLASH_LOGO_DELAY_MS,
            Constants.UI.SPLASH_FADE_DURATION_MS,
        )

        // Fade in Health Connect title
        animateFadeIn(
            binding.tvHealthConnect,
            Constants.UI.SPLASH_TITLE_DELAY_MS,
            Constants.UI.SPLASH_FADE_DURATION_MS,
        )

        // Fade in Body Temperature subtitle
        animateFadeIn(
            binding.tvBodyTemperature,
            Constants.UI.SPLASH_SUBTITLE_DELAY_MS,
            Constants.UI.SPLASH_FADE_DURATION_MS,
        )

        // Slide up and fade in button
        animateSlideUpAndFadeIn(
            binding.btnGetStarted,
            Constants.UI.SPLASH_BUTTON_START_DELAY_MS,
            Constants.UI.SPLASH_SLIDE_DURATION_MS,
        )
    }

    /**
     * Animates view with fade in effect.
     */
    private fun animateFadeIn(
        view: View,
        startDelay: Long,
        duration: Long,
    ) {
        view.alpha = 0f
        view.animate()
            .alpha(1f)
            .setStartDelay(startDelay)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }

    /**
     * Animates view with slide up and fade in effect.
     */
    private fun animateSlideUpAndFadeIn(
        view: View,
        startDelay: Long,
        duration: Long,
    ) {
        view.alpha = 0f
        view.translationY = Constants.UI.SPLASH_TRANSLATION_Y

        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(startDelay)
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()
    }
}
