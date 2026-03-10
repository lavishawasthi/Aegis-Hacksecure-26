package com.obsidian.aegis.ui.risk

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.obsidian.aegis.helpers.SensorRuleEngine
import com.obsidian.aegis.models.SensorAppScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SensorAnalysisViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager = application.packageManager

    val sensorAppScores = MutableLiveData<List<SensorAppScore>>()
    val isLoading = MutableLiveData<Boolean>()

    fun loadSensorData() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            val scoreList = mutableListOf<SensorAppScore>()

            for (appInfo in installedApps) {
                val packageName = appInfo.packageName
                val appName = packageManager.getApplicationLabel(appInfo).toString()
                
                // Skip our own app or common systemic apps if needed, but for now scan all.
                
                val requestedSensors = mutableSetOf<String>()
                try {
                    val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                    val requestedPermissions = packageInfo.requestedPermissions
                    val requestedPermissionsFlags = packageInfo.requestedPermissionsFlags
                    
                    if (requestedPermissions != null && requestedPermissionsFlags != null) {
                        for (i in requestedPermissions.indices) {
                            val perm = requestedPermissions[i]
                            val flag = requestedPermissionsFlags[i]
                            
                            // Check if permission is actually GRANTED to the app
                            if ((flag and android.content.pm.PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                                val sensorName = SensorRuleEngine.mapPermissionToSensor(perm)
                                if (sensorName != null) {
                                    requestedSensors.add(sensorName)
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    continue
                }

                // If the app requests NO sensors of interest, we can skip it to reduce clutter
                if (requestedSensors.isEmpty()) {
                    continue
                }

                val requiredSensors = SensorRuleEngine.getMinimumRequiredSensors(appInfo, appName)
                val extraSensors = requestedSensors.filter { it !in requiredSensors }
                val isSuspicious = extraSensors.isNotEmpty()
                
                // ML Inferencing for Smart Category
                val smartCategory = com.obsidian.aegis.helpers.SmartAppClassifier.classify(appName, packageName)

                scoreList.add(
                    SensorAppScore(
                        appId = packageName,
                        appName = appName,
                        grantedSensors = requestedSensors.toList(),
                        minimumRequiredSensors = requiredSensors,
                        extraSuspiciousSensors = extraSensors,
                        isSuspicious = isSuspicious,
                        smartCategory = smartCategory
                    )
                )
            }

            // Sort so suspicious apps appear at the top
            scoreList.sortByDescending { it.isSuspicious }

            withContext(Dispatchers.Main) {
                sensorAppScores.value = scoreList
                isLoading.value = false
            }
        }
    }
}
