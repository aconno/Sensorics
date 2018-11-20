package com.aconno.sensorics.data.repository

import android.content.res.AssetManager
import com.aconno.sensorics.domain.ResourcesInitializer
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

        moveFolder("configs")
        moveFolder("formats")
        moveFolder("icons")
        moveFolder("device_screens")
        moveFolder("usecase_screens")
        moveFolder("html_resources")
    }

    private fun moveFolder(folderName: String) {
        File("${cacheDir.absolutePath}/sensorics/$folderName/")
            .mkdir()

        assets.list("resources/$folderName")?.let { fileList ->
            fileList.forEach { filename ->
                val fileInputStream = assets.open("resources/$folderName/$filename")
                val fileToBeWritten =
                    File("${cacheDir.absolutePath}/sensorics/$folderName/$filename")

                //Saving IS into the file
                fileToBeWritten.outputStream().use { fileInputStream.copyTo(it) }
            }
        }
    }
}