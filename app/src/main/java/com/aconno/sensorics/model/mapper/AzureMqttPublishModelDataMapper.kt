package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.GeneralAzureMqttPublish
import com.aconno.sensorics.model.AzureMqttPublishModel
import javax.inject.Inject

class AzureMqttPublishModelDataMapper @Inject constructor() {

    fun toAzureMqttPublish(azureMqttPublishModel: AzureMqttPublishModel): AzureMqttPublish {
        return GeneralAzureMqttPublish(
            azureMqttPublishModel.id,
            azureMqttPublishModel.name,
            azureMqttPublishModel.iotHubName,
            azureMqttPublishModel.deviceId,
            azureMqttPublishModel.sharedAccessKey,
            azureMqttPublishModel.enabled,
            azureMqttPublishModel.timeType,
            azureMqttPublishModel.timeMillis,
            azureMqttPublishModel.lastTimeMillis,
            azureMqttPublishModel.dataString
        )
    }

    fun toAzureMqttPublishModel(azureMqttPublish: AzureMqttPublish): AzureMqttPublishModel {
        return AzureMqttPublishModel(
            azureMqttPublish.id,
            azureMqttPublish.name,
            azureMqttPublish.iotHubName,
            azureMqttPublish.deviceId,
            azureMqttPublish.sharedAccessKey,
            azureMqttPublish.enabled,
            azureMqttPublish.timeType,
            azureMqttPublish.timeMillis,
            azureMqttPublish.lastTimeMillis,
            azureMqttPublish.dataString
        )
    }

}
