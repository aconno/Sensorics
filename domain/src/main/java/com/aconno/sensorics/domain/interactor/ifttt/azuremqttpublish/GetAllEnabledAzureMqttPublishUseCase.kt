package com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish

import com.aconno.sensorics.domain.ifttt.AzureMqttPublishRepository
import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledAzureMqttPublishUseCase(
    private val azureMqttPublishRepository: AzureMqttPublishRepository
) : SingleUseCase<List<BasePublish>> {
    override fun execute(): Single<List<BasePublish>> {
        return azureMqttPublishRepository.getAllEnabledAzureMqttPublish()
    }
}