package com.obsidian.aegis.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.obsidian.aegis.databinding.ItemWhitelistBinding
import com.obsidian.aegis.ui.whitelist.AppInfo

class WhitelistAdapter(private val onCheckChanged: (AppInfo, Boolean) -> Unit) :
    ListAdapter<AppInfo, WhitelistAdapter.WhitelistViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WhitelistViewHolder {
        val binding = ItemWhitelistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WhitelistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WhitelistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WhitelistViewHolder(private val binding: ItemWhitelistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(appInfo: AppInfo) {
            binding.tvAppName.text = appInfo.name
            binding.tvPackageName.text = appInfo.packageName
            binding.ivAppIcon.setImageDrawable(appInfo.icon)
            
            binding.switchWhitelist.setOnCheckedChangeListener(null)
            binding.switchWhitelist.isChecked = appInfo.isWhitelisted
            
            binding.switchWhitelist.setOnCheckedChangeListener { _, isChecked ->
                appInfo.isWhitelisted = isChecked
                onCheckChanged(appInfo, isChecked)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean {
            return oldItem.isWhitelisted == newItem.isWhitelisted &&
                    oldItem.name == newItem.name
        }
    }
}
