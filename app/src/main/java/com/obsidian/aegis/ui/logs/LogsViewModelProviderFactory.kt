package com.obsidian.aegis.ui.logs

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obsidian.aegis.repository.AccessLogsRepo

class LogsViewModelProviderFactory(
        private val application: Application,
        private val accessLogsRepo: AccessLogsRepo
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccessLogsViewModel::class.java)) {
            return AccessLogsViewModel(application, accessLogsRepo) as T
        }
        if (modelClass.isAssignableFrom(SuspiciousLogsViewModel::class.java)) {
            return SuspiciousLogsViewModel(application, accessLogsRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}