package com.eic.healthconnectdemo.presentation.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.eic.healthconnectdemo.R
import com.eic.healthconnectdemo.databinding.ActivityTemperatureHistoryBinding
import com.eic.healthconnectdemo.presentation.viewmodel.TemperatureHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity for displaying temperature history from Health Connect.
 */
@AndroidEntryPoint
class TemperatureHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTemperatureHistoryBinding
    private val viewModel: TemperatureHistoryViewModel by viewModels()
    private lateinit var adapter: TemperatureHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemperatureHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.temperature_history)
        }
    }

    private fun setupRecyclerView() {
        adapter = TemperatureHistoryAdapter(
            onDeleteClick = { record ->
                showDeleteConfirmationDialog(record.recordId ?: "")
            }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TemperatureHistoryActivity)
            adapter = this@TemperatureHistoryActivity.adapter
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

    private fun updateUI(state: com.eic.healthconnectdemo.presentation.state.TemperatureHistoryUiState) {
        // Loading state
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

        // Records
        if (state.records.isEmpty() && !state.isLoading) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            adapter.submitList(state.records)
        }

        // Error message
        if (state.error != null) {
            Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    private fun showDeleteConfirmationDialog(recordId: String) {
        AlertDialog.Builder(this)
            .setTitle("Delete Record")
            .setMessage("Are you sure you want to delete this temperature record?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteRecord(recordId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
