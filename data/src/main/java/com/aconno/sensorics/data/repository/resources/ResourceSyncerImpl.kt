package com.aconno.sensorics.data.repository.resources

import android.content.SharedPreferences
import com.aconno.sensorics.data.api.ResourcesApi
import com.aconno.sensorics.domain.ResourceSyncer
import java.io.File

class ResourceSyncerImpl(
    private val cacheFilePath: File,
    private val api: ResourcesApi,
    private val sharedPreferences: SharedPreferences
) : ResourceSyncer {

    private var latestVersion = sharedPreferences.getLong(LATEST_VERSION, LATEST_ASSETS_VERSION)

    override fun sync(): Boolean {
        val latestVersionJsonModel = api.getLatestVersion(latestVersion)

        if (latestVersionJsonModel.isUpdateNeeded) {
            updateFiles(latestVersionJsonModel.filesToBeUpdated)
        }

        return latestVersionJsonModel.isUpdateNeeded
    }

    private fun updateFiles(
        filesToBeUpdated: List<LatestVersionJsonModel.FilesToBeUpdatedJsonModel>
    ) {

        var isDownloadFailed = false

        filesToBeUpdated.forEach { model ->
            val downloadedContentInputStream = api.downloadFile(model.fileName)

            if (downloadedContentInputStream != null) {
                val fileToBeSaved =
                    File(cacheFilePath.absolutePath + model.fileName)
                if (!fileToBeSaved.parentFile.exists()) {
                    fileToBeSaved.parentFile.mkdir()
                }

                fileToBeSaved.parentFile.takeIf {
                    !it.exists()
                }?.let {
                    it.mkdirs()
                }

                //Saving IS into the file
                fileToBeSaved.outputStream().use { downloadedContentInputStream.copyTo(it) }

                //If a download failed stop updating LastModified date
                //So next time it can start from where it failed.
                if (isDownloadFailed) {
                    //Successfully saved update Version in SharedPref
                    updateVersion(model.fileLastModifiedDate)
                }
            } else {
                isDownloadFailed = true
            }
        }
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