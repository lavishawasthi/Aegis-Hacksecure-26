package com.obsidian.aegis.models

data class SensorAppScore(
    val appId: String,
    val appName: String,
    val grantedSensors: List<String>,
    val minimumRequiredSensors: List<String>,
    val extraSuspiciousSensors: List<String>,
    val isSuspicious: Boolean,
    val smartCategory: String
)
