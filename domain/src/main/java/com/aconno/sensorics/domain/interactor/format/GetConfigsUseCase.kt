package com.aconno.sensorics.domain.interactor.format

import com.aconno.sensorics.domain.ConfigListManager
import com.aconno.sensorics.domain.model.ResourceConfig

class GetConfigsUseCase(
    private val configListManager: ConfigListManager
) {
    fun execute(): List<ResourceConfig> =
        configListManager.getConfigs()
}
