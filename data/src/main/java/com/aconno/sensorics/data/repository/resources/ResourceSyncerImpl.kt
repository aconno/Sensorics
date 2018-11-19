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

    private var latestVersion = sharedPreferences.getLong(LATEST_VERSION, 0)

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
        filesToBeUpdated.forEach { model ->
            val downloadedContentInputStream = api.downloadFile(model.fileName)

            val fileToBeSaved =
                File(cacheFilePath.absolutePath + model.fileName)
            if (!fileToBeSaved.parentFile.exists()) {
                fileToBeSaved.parentFile.mkdir()
            }

            //Saving IS into the file
            fileToBeSaved.outputStream().use { downloadedContentInputStream.copyTo(it) }

            //Successfully saved update Version in SharedPref
            updateVersion(model.fileLastModifiedDate)
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
    }
}