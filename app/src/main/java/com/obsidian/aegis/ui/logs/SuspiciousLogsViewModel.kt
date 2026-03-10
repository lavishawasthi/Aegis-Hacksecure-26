package com.obsidian.aegis.ui.logs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.obsidian.aegis.repository.AccessLogsRepo
import kotlinx.coroutines.launch

class SuspiciousLogsViewModel(app: Application, private val accessLogsRepo: AccessLogsRepo): AndroidViewModel(app) {

    val allSuspiciousActivities = accessLogsRepo.fetchAllSuspiciousActivities()

    fun clearAll() = viewModelScope.launch {
        accessLogsRepo.clearSuspiciousActivities()
    }
}
