package com.obsidian.aegis.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.obsidian.aegis.R
import com.obsidian.aegis.databinding.AppBarBinding
import com.obsidian.aegis.models.AccessLog
import com.obsidian.aegis.ui.home.HomeActivity
import com.obsidian.aegis.ui.logs.AccessLogsActivity

class AppBarFragment: Fragment(R.layout.app_bar) {

    private lateinit var binding: AppBarBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AppBarBinding.bind(view)

        setUpViews()
        setUpListeners()
    }

    private fun setUpViews() {
        binding.tvBarHeader.text = when(activity){
            is HomeActivity -> "Customize Indicators"
            is AccessLogsActivity -> "Indicator Logs"
            else -> ""
        }
    }

    private fun setUpListeners() {
        binding.ivBackButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}