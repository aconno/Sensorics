package com.aconno.sensorics.data.repository

import android.content.res.AssetManager
import com.aconno.sensorics.domain.ResourcesInitializer
import timber.log.Timber
import java.io.File

class ResourcesInitializerImpl(
    private val cacheDir: File,
    private val assets: AssetManager
) : ResourcesInitializer {

    override fun init() {
        val sensoricsFile = File(cacheDir.absolutePath + "/sensorics")

        if (sensoricsFile.exists()) {
            return
        } else {
            moveFilesFromAssetsToCache()
        }
    }

    private fun moveFilesFromAssetsToCache() {
        File("${cacheDir.absolutePath}/sensorics/")
            .mkdir()

        moveFolder("resources")
    }

    private fun moveFolder(folderName: String) {
        assets.list(folderName)?.let { fileList ->
            if (fileList.isEmpty()) {
                Timber.i("$folderName + --")
                copyFile(folderName)
            } else {
                fileList.forEach { filename ->
                    Timber.i("$folderName/$filename")
                    moveFolder("$folderName/$filename")
                }
            }
        }
    }

    private fun copyFile(folderPath: String) {
        val fileInputStream = assets.open(folderPath)
        val fileToBeWritten =
            File(
                "${cacheDir.absolutePath}/sensorics/${folderPath.replaceFirst("resources/", "")}"
            )

        Timber.i(fileToBeWritten.path)

        fileToBeWritten.parentFile.takeIf {
            !it.exists()
        }?.let {
            it.mkdirs()
        }

        //Saving IS into the file
        fileToBeWritten.outputStream().use { fileInputStream.copyTo(it) }
    }
}