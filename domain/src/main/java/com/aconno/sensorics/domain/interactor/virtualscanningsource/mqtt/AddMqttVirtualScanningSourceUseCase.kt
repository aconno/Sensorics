package com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt

import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSourceRepository
import io.reactivex.Single

class AddMqttVirtualScanningSourceUseCase (
        private val mqttSourceRepository: MqttVirtualScanningSourceRepository
) : SingleUseCaseWithParameter<Long, MqttVirtualScanningSource> {

    override fun execute(parameter: MqttVirtualScanningSource): Single<Long> {
        return Single.fromCallable {
            mqttSourceRepository.addMqttVirtualScanningSource(parameter)
        }
    }
}