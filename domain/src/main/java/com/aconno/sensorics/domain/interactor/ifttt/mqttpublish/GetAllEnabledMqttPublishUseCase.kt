package com.aconno.sensorics.domain.interactor.ifttt.mqttpublish

import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.publish.MqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledMqttPublishUseCase(
    private val mqttPublishRepository: MqttPublishRepository
) : SingleUseCase<List<MqttPublish>> {
    override fun execute(): Single<List<MqttPublish>> {
        return mqttPublishRepository.allEnabled
    }
}