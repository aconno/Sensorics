package com.aconno.sensorics.data.repository.resources

import com.aconno.sensorics.data.mapper.ConfigFileJsonModelConverter
import com.aconno.sensorics.data.mapper.FormatJsonConverter
import com.aconno.sensorics.data.repository.resources.format.FormatJsonModel
import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.model.ResourceConfig
import com.aconno.sensorics.domain.repository.ResourcesRepository
import com.google.gson.Gson
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException

class ResourcesRepositoryImpl(
    private val cacheFilePath: File,
    private val gson: Gson,
    private val configFileJsonModelConverter: ConfigFileJsonModelConverter,
    private val formatJsonConverter: FormatJsonConverter,
    private val fileStorage: FileStorage
) :
    ResourcesRepository {

    override fun addConfig(resourceConfig : ResourceConfig) {
        val configFolder = File(cacheFilePath.absolutePath + CONFIGS_FILE_PATH)

        if (configFolder.exists()) {
            val configFile = File("${configFolder.path}/${resourceConfig.id}.config")
            val configFileJsonModel = configFileJsonModelConverter.toConfigFileJsonModel(resourceConfig)
            val configFileContent = gson.toJson(configFileJsonModel,ConfigFileJsonModel::class.java)

            fileStorage.storeData(configFile.toURI().toString(),configFileContent.toByteArray())
        } else {
            throw FileNotFoundException("Folder containing configuration files not found.")
        }
    }

    override fun getConfigs(): List<ResourceConfig> {
        val configFolder = File(cacheFilePath.absolutePath + CONFIGS_FILE_PATH)
        if (configFolder.exists()) {
            return configFolder.listFiles()
                .map {
                    Timber.d("Loading config: ${it.path}")

                    val configFileJsonModel = gson.fromJson(
                        it.readText(),
                        ConfigFileJsonModel::class.java
                    )

                    configFileJsonModelConverter.toResourceConfig(configFileJsonModel)
                }
        } else {
            throw FileNotFoundException("Configs not found...")
        }
    }

    override fun addFormat(advertisementFormat: AdvertisementFormat) {
        val formatsFolder = File(cacheFilePath.absolutePath + FORMATS_FILE_PATH)

        if (formatsFolder.exists()) {
            val formatFile = File("${formatsFolder.path}/${advertisementFormat.id}.json")
            val formatFileJsonModel = formatJsonConverter.toAdvertisementFormatJsonModel(advertisementFormat)
            val formatFileContent = gson.toJson(formatFileJsonModel,FormatJsonModel::class.java)

            fileStorage.storeData(formatFile.toURI().toString(),formatFileContent.toByteArray())
        } else {
            throw FileNotFoundException("Folder containing advertisement formats not found.")
        }
    }

    override fun getFormats(): List<AdvertisementFormat> {
        val configFolder = File(cacheFilePath.absolutePath + FORMATS_FILE_PATH)
        if (configFolder.exists()) {
            return configFolder.listFiles()
                .map {
                    Timber.d("Loading format: ${it.path}")

                    val formatFileJsonModel = gson.fromJson(
                        it.readText(),
                        FormatJsonModel::class.java
                    )

                    formatJsonConverter.toAdvertisementFormat(formatFileJsonModel)
                }
        } else {
            throw FileNotFoundException("Formats not found...")
        }
    }

    override fun addMainScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addUseCase() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val CONFIGS_FILE_PATH = "/sensorics/configs/"
        const val FORMATS_FILE_PATH = "/sensorics/formats/"
        const val ICONS_FILE_PATH = "/sensorics/icons/"
        const val MAIN_SCREEN_FILE_PATH = "/sensorics/device_screens/"
        const val USECASE_SCREEN_FILE_PATH = "/sensorics/usecase_screens/"
    }
}