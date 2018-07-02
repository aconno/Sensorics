package com.aconno.acnsensa.domain.interactor.ifttt.mpublish

import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.MqttPublishRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllMqttPublishUseCase(
    private val mqttPublishRepository: MqttPublishRepository
) : SingleUseCase<List<BasePublish>> {
    override fun execute(): Single<List<BasePublish>> {
        return mqttPublishRepository.getAllMqttPublish()
    }
}