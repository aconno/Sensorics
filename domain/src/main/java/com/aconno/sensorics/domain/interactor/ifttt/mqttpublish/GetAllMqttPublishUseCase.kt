package com.aconno.sensorics.domain.interactor.ifttt.mqttpublish

import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllMqttPublishUseCase(
    private val mqttPublishRepository: MqttPublishRepository
) : SingleUseCase<List<MqttPublish>> {
    override fun execute(): Single<List<MqttPublish>> {
        return mqttPublishRepository.all
    }
}