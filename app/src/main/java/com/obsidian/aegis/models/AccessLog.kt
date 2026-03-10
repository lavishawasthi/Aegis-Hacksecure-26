package com.obsidian.aegis.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Ignore

@Entity(
        tableName = "access_logs"
)
data class AccessLog(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val time: Long,
        val durationMs: Long = 0L,
        val appId: String,
        val appName: String,
        val indicatorType: IndicatorType
) {
    @Ignore
    constructor(time: Long, durationMs: Long, appId: String, appName: String, indicatorType: IndicatorType) : this(0, time, durationMs, appId, appName, indicatorType)
    
    @Ignore
    constructor(time: Long, appId: String, appName: String, indicatorType: IndicatorType) : this(0, time, 0L, appId, appName, indicatorType) // Backwards comp
}
