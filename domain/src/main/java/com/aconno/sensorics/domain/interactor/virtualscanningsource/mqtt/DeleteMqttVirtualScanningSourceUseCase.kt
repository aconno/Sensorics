package com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt

import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSourceRepository
import io.reactivex.Completable

class DeleteMqttVirtualScanningSourceUseCase (
        private val mqttSourceRepository: MqttVirtualScanningSourceRepository
): CompletableUseCaseWithParameter<MqttVirtualScanningSource> {

    override fun execute(parameter: MqttVirtualScanningSource): Completable {
        return Completable.fromAction {
            mqttSourceRepository.deleteMqttVirtualScanningSource(parameter)
        }
    }
}