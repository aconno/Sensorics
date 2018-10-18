package com.aconno.sensorics.device.usecase

import android.content.Context
import com.aconno.sensorics.domain.repository.LocalUseCaseRepository
import java.io.File

class LocalUseCaseRepositoryImpl(
    context: Context
) : LocalUseCaseRepository {

    private val sensoricsFilesPath = context.cacheDir.absolutePath + "SensoricsFiles"
    private val sensoricsFolder = File(sensoricsFilesPath)

    init {
        if (!sensoricsFolder.exists()) {
            sensoricsFolder.mkdir()
        }
    }

    override fun saveOrReplaceUseCase(name: String, lastModifiedDate: Long, html: String) {
        val file = File("$sensoricsFilesPath/$name.html")

        file.setLastModified(lastModifiedDate)

        file.printWriter().use {
            it.print(html)
        }
    }

    override fun getLastUpdateTimestamp(sensorName: String): Long? {
        return sensoricsFolder.listFiles()
            .find { it.name.startsWith(sensorName) }?.lastModified()
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