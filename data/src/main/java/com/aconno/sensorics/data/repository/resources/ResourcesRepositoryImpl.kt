package com.aconno.sensorics.data.repository.resources

import com.aconno.sensorics.data.mapper.ConfigFileJsonModelConverter
import com.aconno.sensorics.data.mapper.FormatJsonConverter
import com.aconno.sensorics.data.repository.resources.format.FormatJsonModel
import com.aconno.sensorics.domain.format.AdvertisementFormat
import com.aconno.sensorics.domain.model.ResourceConfig
import com.aconno.sensorics.domain.repository.ResourcesRepository
import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException

class ResourcesRepositoryImpl(
    private val cacheFilePath: File,
    private val gson: Gson,
    private val configFileJsonModelConverter: ConfigFileJsonModelConverter,
    private val formatJsonConverter: FormatJsonConverter
) :
    ResourcesRepository {

    override fun addConfig() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getConfigs(): List<ResourceConfig> {
        val configFolder = File(cacheFilePath.absolutePath + CONFIGS_FILE_PATH)
        if (configFolder.exists()) {
            return configFolder.listFiles()
                .map {

                    val configFileJsonModel = gson.fromJson<ConfigFileJsonModel>(
                        it.readText(),
                        ConfigFileJsonModel::class.java
                    )

                    configFileJsonModelConverter.toResourceConfig(configFileJsonModel)
                }
        } else {
            throw FileNotFoundException("Configs not found..")
        }
    }

    override fun addFormat() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFormats(): List<AdvertisementFormat> {
        val configFolder = File(cacheFilePath.absolutePath + FORMATS_FILE_PATH)
        if (configFolder.exists()) {
            return configFolder.listFiles()
                .map {

                    val formatFileJsonModel = gson.fromJson<FormatJsonModel>(
                        it.readText(),
                        FormatJsonModel::class.java
                    )

                    formatJsonConverter.toAdvertisementFormat(formatFileJsonModel)
                }
        } else {
            throw FileNotFoundException("Formats not found..")
        }
    }

    override fun addMainScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addUseCase() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        const val CONFIGS_FILE_PATH = "/configs/"
        const val FORMATS_FILE_PATH = "/formats/"
        const val ICONS_FILE_PATH = "/icons/"
        const val MAIN_SCREEN_FILE_PATH = "/device_screens/"
        const val USECASE_SCREEN_FILE_PATH = "/usecase_screens/"
    }
}