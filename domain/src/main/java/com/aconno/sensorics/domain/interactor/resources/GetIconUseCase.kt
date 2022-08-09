package com.aconno.sensorics.domain.interactor.resources

import com.aconno.sensorics.domain.ConfigListManager

class GetIconUseCase(
    private val configListManager: ConfigListManager,
    private val cacheDirPath: String
) {

    fun execute(deviceName: String): String? {
        return configListManager.getConfigs()
            .find {
                it.name == deviceName
            }?.let {
                cacheDirPath + it.iconPath
            }
    }
}