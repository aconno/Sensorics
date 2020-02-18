package com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish

import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.AzureMqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class AddAzureMqttPublishUseCase(private val azureMqttPublishRepository: AzureMqttPublishRepository) :
    SingleUseCaseWithParameter<Long, AzureMqttPublish> {
    override fun execute(parameter: AzureMqttPublish): Single<Long> {
        return Single.fromCallable {
            azureMqttPublishRepository.addAzureMqttPublish(parameter)
        }
    }
}