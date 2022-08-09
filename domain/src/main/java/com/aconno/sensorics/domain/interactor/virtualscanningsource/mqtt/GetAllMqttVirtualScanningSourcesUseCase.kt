package com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt

import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import com.aconno.sensorics.domain.virtualscanningsources.BaseVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSourceRepository
import io.reactivex.Single

class GetAllMqttVirtualScanningSourcesUseCase(
        private val mqttSourceRepository: MqttVirtualScanningSourceRepository
) : SingleUseCase<List<BaseVirtualScanningSource>> {

    override fun execute(): Single<List<BaseVirtualScanningSource>> {
        return mqttSourceRepository.getAllMqttVirtualScanningSource()
    }
}