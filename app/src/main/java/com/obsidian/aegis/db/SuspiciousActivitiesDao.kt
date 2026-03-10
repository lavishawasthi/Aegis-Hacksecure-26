package com.obsidian.aegis.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obsidian.aegis.models.SuspiciousActivity

@Dao
interface SuspiciousActivitiesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(activity: SuspiciousActivity): Long

    @Query("SELECT * FROM suspicious_activities ORDER BY time DESC")
    fun getAllSuspiciousActivities(): LiveData<List<SuspiciousActivity>>

    @Query("SELECT * FROM suspicious_activities WHERE time > :since ORDER BY time DESC")
    suspend fun getActivitiesSince(since: Long): List<SuspiciousActivity>

    @Query("DELETE FROM suspicious_activities")
    suspend fun clearAll()
}
