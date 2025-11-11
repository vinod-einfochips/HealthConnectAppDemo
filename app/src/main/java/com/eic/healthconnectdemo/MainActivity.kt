package com.eic.healthconnectdemo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.eic.healthconnectdemo.databinding.ActivityMainBinding
import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import com.eic.healthconnectdemo.presentation.ui.TemperatureHistoryActivity
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
        val allGranted = granted.containsAll(REQUIRED_PERMISSIONS)
        viewModel.setPermissionGranted(allGranted)
        updatePermissionStatus(allGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
        checkHealthConnectAvailability()
    }

    override fun onResume() {
        super.onResume()
        // Check permissions when returning to the app
        checkCurrentPermissions()
    }

    private fun setupViews() {
        // Temperature input listener
        binding.etTemperature.doAfterTextChanged { text ->
            viewModel.updateTemperatureValue(text?.toString() ?: "")
        }

        // Record button
        binding.btnRecordTemperature.setOnClickListener {
            val tempText = binding.etTemperature.text?.toString() ?: ""
            if (tempText.isEmpty()) {
                binding.tilTemperature.error = getString(R.string.enter_temperature)
                return@setOnClickListener
            }
            
            val temp = tempText.toDoubleOrNull()
            if (temp == null) {
                binding.tilTemperature.error = getString(R.string.enter_valid_number)
                return@setOnClickListener
            }
            
            binding.tilTemperature.error = null
            viewModel.recordTemperature(temp, TemperatureUnit.CELSIUS)
        }

        // View history button
        binding.btnViewHistory.setOnClickListener {
            val intent = Intent(this, TemperatureHistoryActivity::class.java)
            startActivity(intent)
        }

        // Check permissions button
        binding.btnCheckPermissions.setOnClickListener {
            checkAndRequestPermissions()
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
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.btnRecordTemperature.isEnabled = !state.isLoading && state.permissionGranted

        // Success message
        if (state.isSuccess) {
            Toast.makeText(
                this,
                getString(R.string.temperature_recorded),
                Toast.LENGTH_SHORT
            ).show()
            binding.etTemperature.text?.clear()
        }

        // Error message
        if (state.error != null) {
            Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }

        // Update permission status
        updatePermissionStatus(state.permissionGranted)
    }

    private fun updatePermissionStatus(granted: Boolean) {
        binding.tvPermissionStatus.text = if (granted) {
            getString(R.string.health_connect_granted)
        } else {
            getString(R.string.health_connect_not_granted)
        }
    }

    /**
     * Checks if Health Connect is available on the device.
     */
    private fun checkHealthConnectAvailability() {
        when (HealthConnectClient.getSdkStatus(this)) {
            HealthConnectClient.SDK_UNAVAILABLE -> {
                showHealthConnectUnavailableDialog()
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                showHealthConnectUpdateDialog()
            }
            else -> {
                // Health Connect is available, check permissions
                checkCurrentPermissions()
            }
        }
    }

    /**
     * Checks current permission status without requesting.
     */
    private fun checkCurrentPermissions() {
        lifecycleScope.launch {
            try {
                val granted = checkPermissions()
                viewModel.setPermissionGranted(granted)
                updatePermissionStatus(granted)
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error checking permissions: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Requests Health Connect permissions.
     */
    private fun checkAndRequestPermissions() {
        lifecycleScope.launch {
            try {
                val granted = checkPermissions()
                if (granted) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.permissions_already_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.setPermissionGranted(true)
                    updatePermissionStatus(true)
                } else {
                    // Request permissions through Health Connect
                    requestHealthConnectPermissions.launch(REQUIRED_PERMISSIONS)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Checks if all required permissions are granted.
     */
    private suspend fun checkPermissions(): Boolean {
        val healthConnectClient = HealthConnectClient.getOrCreate(this)
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(REQUIRED_PERMISSIONS)
    }

    /**
     * Shows dialog when Health Connect is not available.
     */
    private fun showHealthConnectUnavailableDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.health_connect_not_available))
            .setMessage(getString(R.string.health_connect_not_available_message))
            .setPositiveButton(getString(R.string.open_play_store)) { _, _ ->
                openPlayStore()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /**
     * Shows dialog when Health Connect needs update.
     */
    private fun showHealthConnectUpdateDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.health_connect_required))
            .setMessage(getString(R.string.health_connect_install_message))
            .setPositiveButton(getString(R.string.open_play_store)) { _, _ ->
                openPlayStore()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    /**
     * Opens Play Store to install/update Health Connect.
     */
    private fun openPlayStore() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.google.android.apps.healthdata")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        } catch (e: Exception) {
            // If Play Store app is not available, open in browser
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata")
            }
            startActivity(intent)
        }
    }

    companion object {
        /**
         * Required Health Connect permissions for this app.
         */
        val REQUIRED_PERMISSIONS = setOf(
            HealthPermission.getWritePermission(BodyTemperatureRecord::class),
            HealthPermission.getReadPermission(BodyTemperatureRecord::class)
        )
    }
}
