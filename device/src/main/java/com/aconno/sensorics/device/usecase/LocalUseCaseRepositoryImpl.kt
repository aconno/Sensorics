package com.aconno.sensorics.device.usecase

import android.content.Context
import android.content.res.AssetManager
import com.aconno.sensorics.domain.repository.LocalUseCaseRepository
import java.io.File

class LocalUseCaseRepositoryImpl(
    context: Context
) : LocalUseCaseRepository {

    private val sensoricsFilesPath = context.cacheDir.absolutePath + "/" + "SensoricsFiles"
    private val sensoricsFolder = File(sensoricsFilesPath)
    private val assetManager = context.assets
    private val assetPath = "usecases"
    private val timestampOfUseCases = 1537873153000L//25.09.2018 10:59:13

    init {
        if (!sensoricsFolder.exists()) {
            sensoricsFolder.mkdir()
        }
    }

    override fun moveUsecasesFromAssetsToCache() {
        if (sensoricsFolder.list().isEmpty()) {
            //Copies all usecases into cache folder
            assetManager.list(assetPath)?.forEach {
                val fileData = getFileData(assetManager, "$assetPath/$it")

                val file = File("$sensoricsFilesPath/$it")
                file.setLastModified(timestampOfUseCases)
                file.printWriter().use { out ->
                    out.print(fileData)
                }
            }
        }
    }

    private fun getFileData(
        assetManager: AssetManager,
        fileName: String
    ): String {
        val inputStream = assetManager.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }

    override fun saveOrReplaceUseCase(name: String, lastModifiedDate: Long, html: String) {
        val file = File("$sensoricsFilesPath/$name.html")

        file.setLastModified(lastModifiedDate)

        file.printWriter().use {
            it.print(html)
        }
    }

    override fun getFilePathFor(sensorName: String): String {
        val file = File("$sensoricsFilesPath/${sensorName.toLowerCase()}.html")
        return if (file.exists()) {
            "$sensoricsFilesPath/${sensorName.toLowerCase()}.html"
        } else {
            ""
        }
    }

    override fun getLastUpdateTimestamp(sensorName: String): Long? {
        val find = sensoricsFolder.listFiles()
            .find { it.name.startsWith(sensorName) }

        return find?.lastModified() ?: 0
    }

    override fun getAllUseCaseNames(): List<String> {
        return sensoricsFolder.list().map { it.replace(".html", "") }
    }

    override fun deleteUseCase(sensorName: String) {
        val fileToBeDeleted = File("$sensoricsFilesPath/$sensorName.html")
        if (fileToBeDeleted.exists()) {
            fileToBeDeleted.delete()
        }
    }
}