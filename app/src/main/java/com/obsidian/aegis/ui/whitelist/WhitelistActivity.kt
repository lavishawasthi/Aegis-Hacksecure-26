package com.obsidian.aegis.ui.whitelist

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.obsidian.aegis.databinding.ActivityWhitelistBinding
import com.obsidian.aegis.repository.SharedPrefManager
import com.obsidian.aegis.ui.adapters.WhitelistAdapter
import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    var isWhitelisted: Boolean
)

class WhitelistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWhitelistBinding
    private lateinit var sharedPrefManager: SharedPrefManager
    private lateinit var adapter: WhitelistAdapter
    private var allApps: List<AppInfo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWhitelistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefManager = SharedPrefManager.getInstance(this)
        
        setUpViews()
        loadApps()
    }

    private fun setUpViews() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        adapter = WhitelistAdapter { appInfo, isChecked ->
            if (isChecked) {
                sharedPrefManager.addAppToWhitelist(appInfo.packageName)
            } else {
                sharedPrefManager.removeAppFromWhitelist(appInfo.packageName)
            }
        }
        binding.rvApps.layoutManager = LinearLayoutManager(this)
        binding.rvApps.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun filterList(query: String?) {
        val filteredList = if (query.isNullOrBlank()) {
            allApps
        } else {
            allApps.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.packageName.contains(query, ignoreCase = true)
            }
        }
        adapter.submitList(filteredList)
    }

    private fun loadApps() {
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            val pm = packageManager
            val packages = pm.getInstalledPackages(0)
            val appList = packages.mapNotNull { pkg ->
                val app = pkg.applicationInfo
                if (app != null) {
                    AppInfo(
                        name = app.loadLabel(pm).toString(),
                        packageName = app.packageName,
                        icon = app.loadIcon(pm),
                        isWhitelisted = sharedPrefManager.isAppWhitelisted(app.packageName)
                    )
                } else null
            }.sortedBy { it.name }

            withContext(Dispatchers.Main) {
                if (!isFinishing) {
                    allApps = appList
                    filterList(binding.searchView.query?.toString())
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }
}
