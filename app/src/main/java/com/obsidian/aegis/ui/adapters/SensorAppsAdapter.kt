package com.obsidian.aegis.ui.adapters

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.obsidian.aegis.databinding.ItemSensorAppBinding
import com.obsidian.aegis.models.SensorAppScore

class SensorAppsAdapter : RecyclerView.Adapter<SensorAppsAdapter.SensorAppViewHolder>() {

    inner class SensorAppViewHolder(val binding: ItemSensorAppBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<SensorAppScore>() {
        override fun areItemsTheSame(oldItem: SensorAppScore, newItem: SensorAppScore): Boolean {
            return oldItem.appId == newItem.appId
        }

        override fun areContentsTheSame(oldItem: SensorAppScore, newItem: SensorAppScore): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorAppViewHolder {
        val binding = ItemSensorAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SensorAppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SensorAppViewHolder, position: Int) {
        val score = differ.currentList[position]
        holder.binding.apply {
            tvAppName.text = score.appName
            tvAppId.text = score.appId
            tvSmartCategory.text = score.smartCategory
            
            tvRequestedSensors.text = "Sensors Used: ${if (score.grantedSensors.isEmpty()) "None" else score.grantedSensors.joinToString(", ")}"
            tvRequiredSensors.text = "Required: ${if (score.minimumRequiredSensors.isEmpty()) "None" else score.minimumRequiredSensors.joinToString(", ")}"
            
            if (score.isSuspicious) {
                tvExtraSensors.visibility = View.VISIBLE
                tvExtraSensors.text = "Extra (Suspicious): ${score.extraSuspiciousSensors.joinToString(", ")}"
                
                // Highlight red if suspicious
                tvStatus.text = "SUSPICIOUS"
                tvStatus.setBackgroundResource(com.obsidian.aegis.R.drawable.rounded_bg_danger)
            } else {
                tvExtraSensors.visibility = View.GONE
                tvStatus.text = "OK"
                tvStatus.setBackgroundResource(com.obsidian.aegis.R.drawable.rounded_bg_safe)
            }

            btnReviewApp.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:${score.appId}")
                }
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
