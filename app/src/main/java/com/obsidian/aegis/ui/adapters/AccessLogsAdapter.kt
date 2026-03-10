package com.obsidian.aegis.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.obsidian.aegis.databinding.ItemAccessLogsBinding
import com.obsidian.aegis.helpers.toDate
import com.obsidian.aegis.helpers.toTime
import com.obsidian.aegis.models.AccessLog

class AccessLogsAdapter: RecyclerView.Adapter<AccessLogsAdapter.AccessLogsViewHolder>() {

    inner class AccessLogsViewHolder(val binding: ItemAccessLogsBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object :DiffUtil.ItemCallback<AccessLog>() {
        override fun areItemsTheSame(oldItem: AccessLog, newItem: AccessLog): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AccessLog, newItem: AccessLog): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccessLogsViewHolder {
        val binding = ItemAccessLogsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        )
        return AccessLogsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccessLogsViewHolder, position: Int) {
        val log = differ.currentList[position]
        holder.binding.apply {
            tvTimeStamp.text = log.time.toTime()
            tvDate.text = log.time.toDate()
            tvAppId.text = log.appId
            tvAppName.text = log.appName
            ivIndicator.setImageResource(log.indicatorType.drawable)
            tvDuration.text = formatDuration(log.durationMs)
        }
    }

    private fun formatDuration(ms: Long): String {
        if (ms <= 0) return "Duration: < 1s"
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        val hours = (ms / (1000 * 60 * 60))
        return buildString {
            append("Duration: ")
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            if (seconds > 0 || (hours == 0L && minutes == 0L)) append("${seconds}s")
        }.trim()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}