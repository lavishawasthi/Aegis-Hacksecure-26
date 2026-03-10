package com.obsidian.aegis.ui.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.obsidian.aegis.repository.SharedPrefManager

class HomeViewModelProviderFactory(
        private val application: Application,
        private val sharedPrefManager: SharedPrefManager
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(application, sharedPrefManager) as T
    }
}