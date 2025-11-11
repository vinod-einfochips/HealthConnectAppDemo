package com.eic.healthconnectdemo

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eic.healthconnectdemo.databinding.ActivityMainBinding
import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import com.eic.healthconnectdemo.presentation.viewmodel.TemperatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Main activity for the Health Connect Demo app.
 * Handles permission requests and displays the temperature input screen.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: TemperatureViewModel by viewModels()

    private val requestHealthConnectPermissions = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        viewModel.setPermissionGranted(granted.containsAll(REQUIRED_PERMISSIONS))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        checkAndRequestPermissions()
        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        // Temperature input listener
        binding.temperatureInput.doAfterTextChanged { text ->
            viewModel.updateTemperatureValue(text?.toString() ?: "")
        }

        // Unit selection
        binding.unitChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val unit = when (checkedIds.firstOrNull()) {
                binding.fahrenheitChip.id -> TemperatureUnit.FAHRENHEIT
                else -> TemperatureUnit.CELSIUS
            }
            viewModel.updateSelectedUnit(unit)
            
            // Update suffix
            binding.temperatureInputLayout.suffixText = unit.getSymbol()
        }

        // Record button
        binding.recordButton.setOnClickListener {
            val tempText = binding.temperatureInput.text?.toString() ?: ""
            val temp = tempText.toDoubleOrNull()
            if (temp != null) {
                val unit = if (binding.fahrenheitChip.isChecked) {
                    TemperatureUnit.FAHRENHEIT
                } else {
                    TemperatureUnit.CELSIUS
                }
                viewModel.recordTemperature(temp, unit)
            }
        }

        // Dismiss buttons
        binding.dismissErrorButton.setOnClickListener {
            viewModel.clearError()
        }
        
        binding.dismissHealthConnectErrorButton.setOnClickListener {
            viewModel.clearError()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: com.eic.healthconnectdemo.presentation.state.TemperatureUiState) {
        // Loading state
        binding.progressIndicator.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.recordButton.isEnabled = !state.isLoading && state.permissionGranted

        // Health Connect availability
        binding.healthConnectUnavailableCard.visibility = 
            if (!state.healthConnectAvailable) View.VISIBLE else View.GONE

        // Success message
        binding.successCard.visibility = if (state.isSuccess) View.VISIBLE else View.GONE

        // Error message
        if (state.error != null) {
            binding.errorCard.visibility = View.VISIBLE
            binding.errorMessage.text = state.error
        } else {
            binding.errorCard.visibility = View.GONE
        }

        // Update input error state
        binding.temperatureInputLayout.error = if (state.error != null && 
            state.error.contains("Temperature must be")) state.error else null
    }

    /**
     * Checks if permissions are granted and requests them if needed.
     */
    private fun checkAndRequestPermissions() {
        // Request Health Connect permissions
        requestHealthConnectPermissions.launch(REQUIRED_PERMISSIONS)
    }

    companion object {
        /**
         * Required Health Connect permissions for this app.
         */
        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.getWritePermission(BodyTemperatureRecord::class)
        )
    }
}
