package com.obsidian.aegis.ui.logs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.obsidian.aegis.R
import com.obsidian.aegis.databinding.ActivityAccessLogsBinding
import com.obsidian.aegis.db.AccessLogsDatabase
import com.obsidian.aegis.repository.AccessLogsRepo
import com.obsidian.aegis.ui.adapters.AccessLogsAdapter

class AccessLogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccessLogsBinding

    private lateinit var viewModel: AccessLogsViewModel

    private lateinit var accessLogsAdapter: AccessLogsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccessLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val accessLogsDatabase = AccessLogsDatabase(this)
        val viewModelProviderFactory = LogsViewModelProviderFactory(application, AccessLogsRepo(accessLogsDatabase))
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(AccessLogsViewModel::class.java)

        setUpViews()
        setUpListeners()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.allAccessLogs.observe(this, { accessLogsList ->
            accessLogsAdapter.differ.submitList(accessLogsList)
            if(accessLogsList.isEmpty()){
                binding.tvEmptyState.visibility = View.VISIBLE
            }else{
                binding.tvEmptyState.visibility = View.GONE
            }
        })
    }

    private fun setUpViews() {
        accessLogsAdapter = AccessLogsAdapter()
        binding.rvAccessLogs.apply {
            adapter = accessLogsAdapter
            layoutManager = LinearLayoutManager(this@AccessLogsActivity)
        }
    }

    private fun setUpListeners() {
        binding.btnClearAll.setOnClickListener {
            viewModel.clearAllLogs()
        }
    }
}