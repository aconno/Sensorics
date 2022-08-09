package com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt

import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSourceRepository
import io.reactivex.Maybe

class GetMqttVirtualScanningSourceByIdUseCase(
        private val mqttSourceRepository: MqttVirtualScanningSourceRepository
) : MaybeUseCaseWithParameter<MqttVirtualScanningSource, Long> {

    override fun execute(parameter: Long): Maybe<MqttVirtualScanningSource> {
        return mqttSourceRepository.getMqttVirtualScanningSourceById(parameter)
    }
}