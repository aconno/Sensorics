package com.aconno.sensorics.domain.interactor.ifttt.mpublish

import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class AddMqttPublishUseCase(private val mqttPublishRepository: MqttPublishRepository) :
    SingleUseCaseWithParameter<Long, MqttPublish> {
    override fun execute(parameter: MqttPublish): Single<Long> {
        return Single.fromCallable {
            mqttPublishRepository.addMqttPublish(parameter)
        }
    }
}