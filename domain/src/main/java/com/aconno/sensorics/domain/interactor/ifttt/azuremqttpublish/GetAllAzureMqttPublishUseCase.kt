package com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish

import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.publish.AzureMqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllAzureMqttPublishUseCase(
    private val azureMqttPublishRepository: AzureMqttPublishRepository
) : SingleUseCase<List<AzureMqttPublish>> {
    override fun execute(): Single<List<AzureMqttPublish>> {
        return azureMqttPublishRepository.all
    }
}