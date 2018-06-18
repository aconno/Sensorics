package com.aconno.acnsensa.domain.interactor.ifttt.mpublish

import com.aconno.acnsensa.domain.ifttt.MqttPublish
import com.aconno.acnsensa.domain.ifttt.MqttPublishRepository
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class UpdateMqttPublishUseCase(private val mqttPublishRepository: MqttPublishRepository) :
    CompletableUseCaseWithParameter<MqttPublish> {
    override fun execute(parameter: MqttPublish): Completable {
        return Completable.fromAction {
            mqttPublishRepository.updateMqttPublish(parameter)
        }
    }
}