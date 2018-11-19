package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.model.ResourceConfig

interface ConfigRepository {

    fun addConfig()
    fun getConfigs(): List<ResourceConfig>
}