package com.eic.healthconnectdemo.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eic.healthconnectdemo.databinding.ItemTemperatureReadingBinding
import com.eic.healthconnectdemo.domain.model.TemperatureRecord
import com.eic.healthconnectdemo.domain.model.TemperatureUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())

        fun bind(record: TemperatureRecord) {
            // Display temperature in Celsius
            val celsiusTemp =
                when (record.unit) {
                    TemperatureUnit.CELSIUS -> record.temperature
                    TemperatureUnit.FAHRENHEIT -> (record.temperature - 32) * 5 / 9
                }

            // Display temperature in Fahrenheit
            val fahrenheitTemp =
                when (record.unit) {
                    TemperatureUnit.CELSIUS -> (record.temperature * 9 / 5) + 32
                    TemperatureUnit.FAHRENHEIT -> record.temperature
                }

            binding.tvTemperatureCelsius.text = String.format("%.1f°C", celsiusTemp)
            binding.tvTemperatureFahrenheit.text = String.format("%.1f°F", fahrenheitTemp)

            // Format date/time
            val date = Date(record.timestamp.toEpochMilliseconds())
            binding.tvDateTime.text = dateFormat.format(date)

            // Determine status based on temperature (in Celsius)
            val status =
                when {
                    celsiusTemp < 36.1 -> "Low"
                    celsiusTemp in 36.1..37.2 -> "Normal"
                    celsiusTemp in 37.3..38.0 -> "Elevated"
                    else -> "High"
                }
            binding.tvStatus.text = status

            // Set chip color based on status
            val chipColor =
                when (status) {
                    "Low" -> android.R.color.holo_blue_light
                    "Normal" -> android.R.color.holo_green_light
                    "Elevated" -> android.R.color.holo_orange_light
                    else -> android.R.color.holo_red_light
                }
            binding.tvStatus.setChipBackgroundColorResource(chipColor)

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
