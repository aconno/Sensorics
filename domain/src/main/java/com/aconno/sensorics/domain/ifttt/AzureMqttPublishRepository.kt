package com.aconno.sensorics.domain.ifttt

import io.reactivex.Maybe
import io.reactivex.Single

interface AzureMqttPublishRepository {
    fun addAzureMqttPublish(azureMqttPublish: AzureMqttPublish): Long
    fun updateAzureMqttPublish(azureMqttPublish: AzureMqttPublish)
    fun deleteAzureMqttPublish(azureMqttPublish: AzureMqttPublish)
    fun getAllAzureMqttPublish(): Single<List<BasePublish>>
    fun getAllEnabledAzureMqttPublish(): Single<List<BasePublish>>
    fun getAzureMqttPublishById(azureMqttPublishId: Long): Maybe<AzureMqttPublish>
}