package com.obsidian.aegis.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obsidian.aegis.models.AccessLog

@Dao
interface AccessLogsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(accessLog: AccessLog): Long

    @Query("SELECT * FROM access_logs ORDER BY time DESC")
    fun getAllLogs(): LiveData<List<AccessLog>>

    @Query("SELECT SUM(durationMs) FROM access_logs WHERE appId = :appId AND time >= :since")
    suspend fun getSensorDurationForAppSince(appId: String, since: Long): Long?

    @Query("SELECT * FROM access_logs WHERE appId = :appId AND time >= :since ORDER BY time ASC")
    suspend fun getLogsForAppSince(appId: String, since: Long): List<AccessLog>

    @Query("DELETE FROM access_logs")
    suspend fun clearLogs()
}