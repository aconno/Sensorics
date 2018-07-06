package com.aconno.sensorics.domain.interactor.ifttt.mpublish

import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteMqttPublishUseCase(
    private val mqttPublishRepository: MqttPublishRepository
) : CompletableUseCaseWithParameter<MqttPublish> {
    override fun execute(parameter: MqttPublish): Completable {
        return Completable.fromAction {
            mqttPublishRepository.deleteMqttPublish(parameter)
        }
    }
}