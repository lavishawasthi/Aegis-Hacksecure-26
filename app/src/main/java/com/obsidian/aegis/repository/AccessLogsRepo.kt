package com.obsidian.aegis.repository

import com.obsidian.aegis.db.AccessLogsDatabase
import com.obsidian.aegis.models.AccessLog

class AccessLogsRepo(
        val db: AccessLogsDatabase
) {
    suspend fun save(accessLog: AccessLog) = db.getAccessLogsDao().upsert(accessLog)

    suspend fun clear() = db.getAccessLogsDao().clearLogs()

    fun fetchAll() = db.getAccessLogsDao().getAllLogs()

    suspend fun getSensorDurationForAppSince(appId: String, since: Long): Long {
        return db.getAccessLogsDao().getSensorDurationForAppSince(appId, since) ?: 0L
    }

    suspend fun getLogsForAppSince(appId: String, since: Long): List<com.obsidian.aegis.models.AccessLog> {
        return db.getAccessLogsDao().getLogsForAppSince(appId, since)
    }

    suspend fun saveSuspiciousActivity(activity: com.obsidian.aegis.models.SuspiciousActivity) =
        db.getSuspiciousActivitiesDao().insert(activity)

    suspend fun clearSuspiciousActivities() = db.getSuspiciousActivitiesDao().clearAll()

    fun fetchAllSuspiciousActivities() = db.getSuspiciousActivitiesDao().getAllSuspiciousActivities()

    suspend fun fetchRecentSuspiciousActivities(since: Long) =
        db.getSuspiciousActivitiesDao().getActivitiesSince(since)
}