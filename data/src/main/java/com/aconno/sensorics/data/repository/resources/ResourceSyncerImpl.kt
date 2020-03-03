package com.aconno.sensorics.data.repository.resources

import android.content.SharedPreferences
import com.aconno.sensorics.data.api.ResourcesApi
import com.aconno.sensorics.domain.ResourceSyncer
import com.aconno.sensorics.domain.tryUse
import timber.log.Timber
import java.io.File

class ResourceSyncerImpl(
    private val cacheFilePath: File,
    private val api: ResourcesApi,
    private val sharedPreferences: SharedPreferences
) : ResourceSyncer {

    private var latestVersion = getResourcesVersion()

    override fun sync(): Boolean {
        Timber.d("Syncing...")
        val latestVersionJsonModel = api.getLatestVersion(latestVersion)

        if (latestVersionJsonModel.isUpdateNeeded) {
            updateFiles(latestVersionJsonModel.filesToBeUpdated)
        }

        return latestVersionJsonModel.isUpdateNeeded
    }

    private fun updateFiles(
        filesToBeUpdated: List<LatestVersionJsonModel.FilesToBeUpdatedJsonModel>
    ) {
        var allDownloadsSucceeded = true

        filesToBeUpdated.forEach { fileModel ->
            api.downloadFile(fileModel.fileName)?.tryUse({ downloadStream ->
                // Local cache file path
                val filePath = "${cacheFilePath.absolutePath}${fileModel.fileName}"
                val targetFile = File(filePath)

                // Create file path if missing
                if (targetFile.parentFile?.exists() != true) {
                    targetFile.mkdirs()
                }

                // Open local file stream
                targetFile.outputStream().tryUse({ fileStream ->
                    // Copy
                    downloadStream.copyTo(fileStream)

                    if (allDownloadsSucceeded) {
                        updateVersion(fileModel.fileLastModifiedDate)
                    }
                    true
                }, { it ->
                    Timber.e(it, "Failed to open local file stream!")
                    false
                })
            }, {
                Timber.e(it, "Failed to open download stream!")
                false
            }).let { downloadSucceeded ->
                allDownloadsSucceeded = allDownloadsSucceeded and (downloadSucceeded == true)
            }
        }
    }

    private fun getResourcesVersion(): Long {
        return sharedPreferences.getLong(LATEST_VERSION, LATEST_ASSETS_VERSION)
    }

    private fun updateVersion(fileLastModifiedDate: Long) {
        latestVersion = fileLastModifiedDate

        sharedPreferences.edit()
            .putLong(LATEST_VERSION, fileLastModifiedDate)
            .apply()
    }

    companion object {
        const val LATEST_VERSION = "LATEST_VERSION"
        const val LATEST_ASSETS_VERSION = 1557484716L
    }
}