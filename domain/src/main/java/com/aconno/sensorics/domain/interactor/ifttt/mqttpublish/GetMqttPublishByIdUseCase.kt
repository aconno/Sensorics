package com.aconno.sensorics.domain.interactor.ifttt.mqttpublish

import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetMqttPublishByIdUseCase(
    private val mqttPublishRepository: MqttPublishRepository
) : MaybeUseCaseWithParameter<MqttPublish, Long> {
    override fun execute(parameter: Long): Maybe<MqttPublish> {
        return mqttPublishRepository.getPublishById(parameter)
    }
}