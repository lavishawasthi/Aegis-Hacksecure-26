package com.obsidian.aegis.ui.risk

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.obsidian.aegis.db.AccessLogsDatabase
import com.obsidian.aegis.helpers.UsageStatsHelper
import com.obsidian.aegis.models.AppRiskScore
import com.obsidian.aegis.models.RiskLevel
import com.obsidian.aegis.repository.AccessLogsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RiskViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AccessLogsRepo(AccessLogsDatabase(application))
    private val usageStatsHelper = UsageStatsHelper(application)
    private val packageManager = application.packageManager

    val appRiskScores = MutableLiveData<List<AppRiskScore>>()
    val isLoading = MutableLiveData<Boolean>()

    fun loadRiskData() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val since24h = System.currentTimeMillis() - 24 * 60 * 60 * 1000

            // 1. Get all unique apps from access logs in last 24h
            val allLogs = repo.fetchAll().value ?: emptyList() // Note: LiveData synchronous read hack won't work perfectly on IO thread without observer, let's use DAO directly returning List later if needed, or observe it.
            // For this phase, let's fetch all installed apps or just apps that have suspicious activity.
            // Since we want to check all apps, we can iterate installed applications.
            
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            val riskList = mutableListOf<AppRiskScore>()

            for (appInfo in installedApps) {
                val packageName = appInfo.packageName
                val appName = packageManager.getApplicationLabel(appInfo).toString()

                // Calculate Sensor Duration
                val sensorDuration = repo.getSensorDurationForAppSince(packageName, since24h)

                // Calculate Battery Proxy (Foreground time)
                val foregroundTimeMs = usageStatsHelper.getAppUsageTimeLast24Hours(packageName)

                // Check Internet Permission
                val hasInternet = packageManager.checkPermission(android.Manifest.permission.INTERNET, packageName) == PackageManager.PERMISSION_GRANTED

                val scoreAndLevel = calculateRiskScore(sensorDuration, foregroundTimeMs, hasInternet)
                
                riskList.add(
                    AppRiskScore(
                        appId = packageName,
                        appName = appName,
                        sensorDurationMs = sensorDuration,
                        usageTimeMs = foregroundTimeMs,
                        hasInternetPermission = hasInternet,
                        riskScore = scoreAndLevel.first,
                        riskLevel = scoreAndLevel.second
                    )
                )
            }

            // Sort by risk score descending
            riskList.sortByDescending { it.riskScore }

            withContext(Dispatchers.Main) {
                appRiskScores.value = riskList
                isLoading.value = false
            }
        }
    }

    private fun calculateRiskScore(sensorDurationMs: Long, foregroundTimeMs: Long, hasInternet: Boolean): Pair<Int, RiskLevel> {
        var score = 0
        
        // Sensor usage weight: 1 point per minute of background/foreground sensor usage
        val sensorMinutes = (sensorDurationMs / 60000).toInt()
        score += sensorMinutes * 2
        
        // Battery proxy weight: 1 point per 10 minutes of active usage
        val usageMinutes = (foregroundTimeMs / 60000).toInt()
        score += usageMinutes / 10

        // Internet multiplier
        if (hasInternet && sensorDurationMs > 0) {
            score += 20 // High bump if it uses sensors AND has internet
        }

        val level = when {
            score >= 50 -> RiskLevel.HIGH
            score in 20..49 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }

        return Pair(score, level)
    }
}
