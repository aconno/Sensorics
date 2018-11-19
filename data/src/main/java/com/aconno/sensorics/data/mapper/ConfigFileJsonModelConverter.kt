package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.resources.ConfigFileJsonModel
import com.aconno.sensorics.domain.model.ResourceConfig

class ConfigFileJsonModelConverter {

    fun toResourceConfig(configFileJsonModel: ConfigFileJsonModel): ResourceConfig {
        return ResourceConfig(
            configFileJsonModel.deviceScreenPath,
            configFileJsonModel.formatPath,
            configFileJsonModel.iconPath,
            configFileJsonModel.id,
            configFileJsonModel.name,
            configFileJsonModel.usecaseScreenPath
        )
    }
}