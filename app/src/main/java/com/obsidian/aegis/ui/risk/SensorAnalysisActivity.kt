package com.obsidian.aegis.ui.risk

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.obsidian.aegis.databinding.ActivitySensorAnalysisBinding
import com.obsidian.aegis.ui.adapters.SensorAppsAdapter

class SensorAnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySensorAnalysisBinding
    private lateinit var viewModel: SensorAnalysisViewModel
    private lateinit var adapter: SensorAppsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SensorAnalysisViewModel::class.java)
        adapter = SensorAppsAdapter()

        setupRecyclerView()
        setupObservers()

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.loadSensorData()
    }

    private fun setupRecyclerView() {
        binding.rvSensorApps.apply {
            adapter = this@SensorAnalysisActivity.adapter
            layoutManager = LinearLayoutManager(this@SensorAnalysisActivity)
        }
    }

    private fun setupObservers() {
        viewModel.sensorAppScores.observe(this, { scores ->
            adapter.differ.submitList(scores)
            binding.rvSensorApps.visibility = View.VISIBLE
        })

        viewModel.isLoading.observe(this, { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        })
    }
}
