package com.aconno.sensorics.model.mapper

import com.aconno.sensorics.domain.virtualscanningsources.mqtt.GeneralMqttVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import com.aconno.sensorics.model.MqttVirtualScanningSourceModel
import javax.inject.Inject

class MqttVirtualScanningSourceModelDataMapper @Inject constructor(){


    fun toMqttVirtualScanningSourceModel(virtualScanningSource: MqttVirtualScanningSource) : MqttVirtualScanningSourceModel {
        return MqttVirtualScanningSourceModel(
                virtualScanningSource.id,
                virtualScanningSource.name,
                virtualScanningSource.enabled,
                virtualScanningSource.protocol,
                virtualScanningSource.address,
                virtualScanningSource.port,
                virtualScanningSource.path,
                virtualScanningSource.clientId,
                virtualScanningSource.username,
                virtualScanningSource.password,
                virtualScanningSource.qualityOfService
        )
    }

    fun toMqttVirtualScanningSource(virtualScanningSourceModel: MqttVirtualScanningSourceModel) : MqttVirtualScanningSource {
        return GeneralMqttVirtualScanningSource(
                virtualScanningSourceModel.id,
                virtualScanningSourceModel.name,
                virtualScanningSourceModel.enabled,
                virtualScanningSourceModel.protocol,
                virtualScanningSourceModel.address,
                virtualScanningSourceModel.port,
                virtualScanningSourceModel.path,
                virtualScanningSourceModel.clientId,
                virtualScanningSourceModel.username,
                virtualScanningSourceModel.password,
                virtualScanningSourceModel.qualityOfService
        )
    }

}