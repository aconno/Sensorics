package com.aconno.sensorics.domain.interactor.ifttt.mqttpublish

import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Maybe
import io.reactivex.Single

class GetMqttPublishByIdUseCase(
    private val mqttPublishRepository: MqttPublishRepository
) : MaybeUseCaseWithParameter<MqttPublish, Long> {
    override fun execute(parameter: Long): Maybe<MqttPublish> {
        return mqttPublishRepository.getMqttPublishById(parameter)
    }
}