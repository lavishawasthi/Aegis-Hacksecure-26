package com.obsidian.aegis.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.obsidian.aegis.models.AccessLog
import com.obsidian.aegis.models.SuspiciousActivity

@Database(
        entities = [AccessLog::class, SuspiciousActivity::class],
        version = 4
)
@TypeConverters(Converters::class)
abstract class AccessLogsDatabase: RoomDatabase() {

    abstract fun getAccessLogsDao():AccessLogsDao
    abstract fun getSuspiciousActivitiesDao(): SuspiciousActivitiesDao

    companion object {
        @Volatile
        private var instance: AccessLogsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext,
                        AccessLogsDatabase::class.java,
                        "access_logs_db.db"
                ).fallbackToDestructiveMigration()
                    .build()
    }
}