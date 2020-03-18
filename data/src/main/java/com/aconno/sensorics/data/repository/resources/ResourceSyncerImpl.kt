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

    private var localVersion = getResourcesVersion()

    override fun sync(): Boolean {
        Timber.d("Syncing...")
        val latestVersionJsonModel = api.getResourceVersionDelta(localVersion)

        if (latestVersionJsonModel.updateNeeded) {
            updateFiles(latestVersionJsonModel.files)
        }

        return latestVersionJsonModel.updateNeeded
    }

    private fun updateFiles(
        files: List<ResourceDelta.VersionedFile>
    ) {
        var allDownloadsSucceeded = true

        files.filter { versionedFile ->
            getFileVersion(versionedFile) < versionedFile.fileLastModifiedDate
        }.forEach { versionedFile ->
            api.downloadFile(versionedFile.fileName)?.tryUse({ downloadStream ->
                // Local cache file path
                val filePath = "${cacheFilePath.absolutePath}${versionedFile.fileName}"
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
                        updateVersion(versionedFile.fileLastModifiedDate)
                    }

                    updateFileVersion(versionedFile)
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
        localVersion = fileLastModifiedDate

        sharedPreferences.edit()
            .putLong(LATEST_VERSION, fileLastModifiedDate)
            .apply()
    }

    private fun getFileVersion(versionedFile: ResourceDelta.VersionedFile): Long {
        return sharedPreferences.getLong("$LATEST_VERSION${versionedFile.fileName}", LATEST_ASSETS_VERSION)
    }

    private fun updateFileVersion(versionedFile: ResourceDelta.VersionedFile) {
        sharedPreferences.edit()
            .putLong("$LATEST_VERSION${versionedFile.fileName}", versionedFile.fileLastModifiedDate)
            .apply()
    }

    companion object {
        const val LATEST_VERSION = "LATEST_VERSION"
        const val LATEST_ASSETS_VERSION = 1557484716L
    }
}