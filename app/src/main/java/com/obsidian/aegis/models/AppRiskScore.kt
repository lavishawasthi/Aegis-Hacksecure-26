package com.obsidian.aegis.models

data class AppRiskScore(
    val appId: String,
    val appName: String,
    val sensorDurationMs: Long,
    val usageTimeMs: Long,
    val hasInternetPermission: Boolean,
    val riskScore: Int,
    val riskLevel: RiskLevel
)

enum class RiskLevel {
    HIGH, MEDIUM, LOW
}
