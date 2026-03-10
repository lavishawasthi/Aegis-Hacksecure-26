package com.obsidian.aegis.ui.risk

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.obsidian.aegis.models.IndicatorType
import com.obsidian.aegis.R
import com.obsidian.aegis.db.AccessLogsDatabase
import com.obsidian.aegis.repository.AccessLogsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RiskBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var appId: String
    private lateinit var appName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            appId = it.getString(ARG_APP_ID) ?: ""
            appName = it.getString(ARG_APP_NAME) ?: ""
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_risk_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvAppName).text = appName
        view.findViewById<TextView>(R.id.tvAppId).text = appId

        val lineChart = view.findViewById<LineChart>(R.id.lineChart)
        val btnRevokeNow = view.findViewById<Button>(R.id.btnRevokeNow)

        setupChart(lineChart)
        loadChartData(lineChart)

        btnRevokeNow.setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$appId")
            }
            startActivity(intent)
            dismiss()
        }
    }

    private fun setupChart(lineChart: LineChart) {
        lineChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setPinchZoom(false)
            setScaleEnabled(false)

            axisRight.isEnabled = false
            
            axisLeft.apply {
                axisMinimum = 0f
                setDrawGridLines(true)
                textColor = Color.DKGRAY
            }

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.DKGRAY
            }
        }
    }

    private fun loadChartData(lineChart: LineChart) {
        lifecycleScope.launch(Dispatchers.IO) {
            val repo = AccessLogsRepo(AccessLogsDatabase(requireContext()))
            val since24h = System.currentTimeMillis() - 24 * 60 * 60 * 1000
            val appLogs = repo.getLogsForAppSince(appId, since24h)
            
            // Bucket logs into 24 hours
            val startOf24h = since24h
            val camBuckets = IntArray(24)
            val micBuckets = IntArray(24)
            val locBuckets = IntArray(24)

            for (log in appLogs) {
                // Determine hour index (0 to 23)
                val hourIndex = ((log.time - startOf24h) / (60 * 60 * 1000)).toInt().coerceIn(0, 23)
                when (log.indicatorType) {
                    IndicatorType.CAMERA -> camBuckets[hourIndex]++
                    IndicatorType.MICROPHONE -> micBuckets[hourIndex]++
                    IndicatorType.LOCATION -> locBuckets[hourIndex]++
                }
            }

            val camEntries = ArrayList<Entry>()
            val micEntries = ArrayList<Entry>()
            val locEntries = ArrayList<Entry>()

            for (i in 0..23) {
                camEntries.add(Entry(i.toFloat(), camBuckets[i].toFloat()))
                micEntries.add(Entry(i.toFloat(), micBuckets[i].toFloat()))
                locEntries.add(Entry(i.toFloat(), locBuckets[i].toFloat()))
            }

            val camDataSet = LineDataSet(camEntries, "Camera").apply {
                color = Color.parseColor("#4CAF50") // Green
                setCircleColor(Color.parseColor("#4CAF50"))
                lineWidth = 2f
            }
            val micDataSet = LineDataSet(micEntries, "Mic").apply {
                color = Color.parseColor("#FF9800") // Orange
                setCircleColor(Color.parseColor("#FF9800"))
                lineWidth = 2f
            }
            val locDataSet = LineDataSet(locEntries, "Location").apply {
                color = Color.parseColor("#2196F3") // Blue
                setCircleColor(Color.parseColor("#2196F3"))
                lineWidth = 2f
            }

            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(camDataSet)
            dataSets.add(micDataSet)
            dataSets.add(locDataSet)

            val lineData = LineData(dataSets)

            withContext(Dispatchers.Main) {
                lineChart.data = lineData
                
                val currentHour = (System.currentTimeMillis() / (60 * 60 * 1000)) % 24
                val hours = (0..23).map { "${(currentHour + it + 1) % 24}h" }.toTypedArray()
                lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(hours)
                
                lineChart.invalidate()
            }
        }
    }

    companion object {
        private const val ARG_APP_ID = "arg_app_id"
        private const val ARG_APP_NAME = "arg_app_name"

        @JvmStatic
        fun newInstance(appId: String, appName: String) =
            RiskBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_APP_ID, appId)
                    putString(ARG_APP_NAME, appName)
                }
            }
    }
}
