package com.obsidian.aegis.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.obsidian.aegis.databinding.ItemRiskAppBinding
import com.obsidian.aegis.models.AppRiskScore
import com.obsidian.aegis.models.RiskLevel

class RiskAppsAdapter : RecyclerView.Adapter<RiskAppsAdapter.RiskAppViewHolder>() {

    inner class RiskAppViewHolder(val binding: ItemRiskAppBinding) : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<AppRiskScore>() {
        override fun areItemsTheSame(oldItem: AppRiskScore, newItem: AppRiskScore): Boolean {
            return oldItem.appId == newItem.appId
        }

        override fun areContentsTheSame(oldItem: AppRiskScore, newItem: AppRiskScore): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskAppViewHolder {
        val binding = ItemRiskAppBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RiskAppViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RiskAppViewHolder, position: Int) {
        val riskScore = differ.currentList[position]
        holder.binding.apply {
            tvRiskAppName.text = riskScore.appName
            tvRiskAppId.text = riskScore.appId
            tvRiskScoreVal.text = "Score: ${riskScore.riskScore}"
            
            tvSensorDuration.text = formatDuration(riskScore.sensorDurationMs)
            tvUsageTime.text = formatDuration(riskScore.usageTimeMs)

            tvRiskLevel.text = riskScore.riskLevel.name
            when (riskScore.riskLevel) {
                RiskLevel.HIGH -> tvRiskLevel.setBackgroundColor(Color.parseColor("#F44336"))
                RiskLevel.MEDIUM -> tvRiskLevel.setBackgroundColor(Color.parseColor("#FF9800"))
                RiskLevel.LOW -> tvRiskLevel.setBackgroundColor(Color.parseColor("#4CAF50"))
            }

            root.setOnClickListener {
                onItemClickListener?.invoke(riskScore)
            }
        }
    }

    private fun formatDuration(ms: Long): String {
        if (ms <= 0) return "0s"
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        val hours = (ms / (1000 * 60 * 60))
        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            if (seconds > 0 || (hours == 0L && minutes == 0L)) append("${seconds}s")
        }.trim()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((AppRiskScore) -> Unit)? = null

    fun setOnItemClickListener(listener: (AppRiskScore) -> Unit) {
        onItemClickListener = listener
    }
}
