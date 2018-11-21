package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.model.ResourceConfig
import com.aconno.sensorics.domain.repository.ConfigRepository

class ConfigListManager(
    val configRepository: ConfigRepository
) {

    var isDirty: Boolean = true

    private var formatList: List<ResourceConfig> = listOf()

    fun getConfigs(): List<ResourceConfig> {
        if (isDirty) {
            formatList = configRepository.getConfigs()
            isDirty = false
        }

        return formatList
    }
}