package com.eic.healthconnectdemo.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eic.healthconnectdemo.core.formatter.DateTimeFormatter
import com.eic.healthconnectdemo.core.util.TemperatureConverter
import com.eic.healthconnectdemo.databinding.ItemTemperatureReadingBinding
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
import com.eic.healthconnectdemo.domain.model.TemperatureStatus

/**
 * RecyclerView adapter for displaying temperature history.
 */
class TemperatureHistoryAdapter(
    private val onDeleteClick: (TemperatureRecord) -> Unit,
) : ListAdapter<TemperatureRecord, TemperatureHistoryAdapter.TemperatureViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): TemperatureViewHolder {
        val binding =
            ItemTemperatureReadingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return TemperatureViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(
        holder: TemperatureViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    class TemperatureViewHolder(
        private val binding: ItemTemperatureReadingBinding,
        private val onDeleteClick: (TemperatureRecord) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(record: TemperatureRecord) {
            // Use centralized temperature converter
            val celsiusTemp = TemperatureConverter.toCelsius(record.temperature, record.unit)
            val fahrenheitTemp = TemperatureConverter.toFahrenheit(record.temperature, record.unit)

            binding.tvTemperatureCelsius.text = String.format("%.1f°C", celsiusTemp)
            binding.tvTemperatureFahrenheit.text = String.format("%.1f°F", fahrenheitTemp)

            // Use centralized date formatter
            binding.tvDateTime.text = DateTimeFormatter.formatDateTime(record.timestamp)

            // Use TemperatureStatus enum for status determination
            val status = TemperatureStatus.fromRecord(record)
            binding.tvStatus.text = status.displayName
            binding.tvStatus.setChipBackgroundColorResource(status.colorRes)

            // Delete button
            binding.btnDelete.setOnClickListener {
                onDeleteClick(record)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<TemperatureRecord>() {
        override fun areItemsTheSame(
            oldItem: TemperatureRecord,
            newItem: TemperatureRecord,
        ): Boolean {
            return oldItem.recordId == newItem.recordId
        }

        override fun areContentsTheSame(
            oldItem: TemperatureRecord,
            newItem: TemperatureRecord,
        ): Boolean {
            return oldItem == newItem
        }
    }
}
