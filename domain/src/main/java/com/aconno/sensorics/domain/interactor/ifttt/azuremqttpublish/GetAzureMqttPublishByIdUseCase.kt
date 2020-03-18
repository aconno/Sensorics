package com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish

import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.AzureMqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetAzureMqttPublishByIdUseCase(
    private val azureMqttPublishRepository: AzureMqttPublishRepository
) : MaybeUseCaseWithParameter<AzureMqttPublish, Long> {
    override fun execute(parameter: Long): Maybe<AzureMqttPublish> {
        return azureMqttPublishRepository.getAzureMqttPublishById(parameter)
    }
}