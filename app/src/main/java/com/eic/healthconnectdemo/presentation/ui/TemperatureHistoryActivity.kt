package com.eic.healthconnectdemo.presentation.ui

import android.animation.ObjectAnimator
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
import com.eic.healthconnectdemo.databinding.LayoutFilterPanelBinding
import com.eic.healthconnectdemo.domain.model.DateRangeFilter
import com.eic.healthconnectdemo.domain.model.SortOption
import com.eic.healthconnectdemo.domain.model.TemperatureRangeFilter
import com.eic.healthconnectdemo.presentation.viewmodel.TemperatureHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Activity for displaying temperature history from Health Connect.
 */
@AndroidEntryPoint
class TemperatureHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTemperatureHistoryBinding
    private lateinit var filterPanelBinding: LayoutFilterPanelBinding
    private val viewModel: TemperatureHistoryViewModel by viewModels()
    private lateinit var adapter: TemperatureHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemperatureHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize filter panel binding
        filterPanelBinding = LayoutFilterPanelBinding.bind(binding.filterPanelInclude.root)

        setupToolbar()
        setupRecyclerView()
        setupFilterPanel()
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

    private fun setupFilterPanel() {
        // Toggle filter panel expansion
        filterPanelBinding.filterPanelHeader.setOnClickListener {
            viewModel.toggleFilterPanel()
        }

        // Sort options
        filterPanelBinding.sortChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chipDateNewest -> viewModel.setSortOption(SortOption.DATE_NEWEST_FIRST)
                    R.id.chipDateOldest -> viewModel.setSortOption(SortOption.DATE_OLDEST_FIRST)
                    R.id.chipTempHighest -> viewModel.setSortOption(SortOption.TEMPERATURE_HIGHEST_FIRST)
                    R.id.chipTempLowest -> viewModel.setSortOption(SortOption.TEMPERATURE_LOWEST_FIRST)
                }
            }
        }

        // Date range filter
        filterPanelBinding.dateRangeChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chipDateAll -> viewModel.setDateRangeFilter(DateRangeFilter.ALL)
                    R.id.chipDateToday -> viewModel.setDateRangeFilter(DateRangeFilter.TODAY)
                    R.id.chipDate7Days -> viewModel.setDateRangeFilter(DateRangeFilter.LAST_7_DAYS)
                    R.id.chipDate30Days -> viewModel.setDateRangeFilter(DateRangeFilter.LAST_30_DAYS)
                }
            }
        }

        // Temperature range filter
        filterPanelBinding.tempRangeChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                when (checkedIds[0]) {
                    R.id.chipTempAll -> viewModel.setTemperatureRangeFilter(TemperatureRangeFilter.ALL)
                    R.id.chipTempLow -> viewModel.setTemperatureRangeFilter(TemperatureRangeFilter.LOW)
                    R.id.chipTempMedium -> viewModel.setTemperatureRangeFilter(TemperatureRangeFilter.MEDIUM)
                    R.id.chipTempHigh -> viewModel.setTemperatureRangeFilter(TemperatureRangeFilter.HIGH)
                }
            }
        }

        // Clear filters button
        filterPanelBinding.btnClearFilters.setOnClickListener {
            viewModel.clearFilters()
            // Reset chip selections
            filterPanelBinding.sortChipGroup.check(R.id.chipDateNewest)
            filterPanelBinding.dateRangeChipGroup.check(R.id.chipDateAll)
            filterPanelBinding.tempRangeChipGroup.check(R.id.chipTempAll)
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

        // Filter panel expansion state
        updateFilterPanelExpansion(state.isFilterPanelExpanded)

        // Clear filters button visibility
        filterPanelBinding.btnClearFilters.visibility = 
            if (state.hasActiveFilters()) View.VISIBLE else View.GONE

        // Records (use filtered records)
        if (state.filteredRecords.isEmpty() && !state.isLoading) {
            binding.emptyView.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
            adapter.submitList(state.filteredRecords)
        }

        // Error message
        if (state.error != null) {
            Toast.makeText(this, state.error, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    private fun updateFilterPanelExpansion(isExpanded: Boolean) {
        filterPanelBinding.filterPanelContent.visibility = 
            if (isExpanded) View.VISIBLE else View.GONE
        
        // Animate toggle icon rotation
        val rotation = if (isExpanded) 180f else 0f
        ObjectAnimator.ofFloat(filterPanelBinding.filterPanelToggle, "rotation", rotation).apply {
            duration = 200
            start()
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
