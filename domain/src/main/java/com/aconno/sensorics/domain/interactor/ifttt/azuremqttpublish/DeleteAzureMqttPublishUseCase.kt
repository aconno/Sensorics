package com.aconno.sensorics.domain.interactor.ifttt.azuremqttpublish

import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.publish.AzureMqttPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteAzureMqttPublishUseCase(
    private val azureMqttPublishRepository: AzureMqttPublishRepository
) : CompletableUseCaseWithParameter<AzureMqttPublish> {
    override fun execute(parameter: AzureMqttPublish): Completable {
        return Completable.fromAction {
            azureMqttPublishRepository.deletePublish(parameter)
        }
    }
}