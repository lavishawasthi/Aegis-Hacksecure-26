package com.obsidian.aegis.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suspicious_activities")
data class SuspiciousActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val time: Long,
    val appId: String,
    val appName: String,
    val description: String,
    val riskLevel: String, // High, Critical, Tracking
    val isScreenOff: Boolean = false
)
