package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.ifttt.publish.*
import io.reactivex.Single

class GetRepositoryForPublishUseCase(
    private val azureMqttPublishRepository: AzureMqttPublishRepository,
    private val googlePublishRepository: GooglePublishRepository,
    private val mqttPublishRepository: MqttPublishRepository,
    private val restPublishRepository: RestPublishRepository
) {
    fun <P : BasePublish> execute(parameter: P): Single<PublishRepository<P>> {
        return Single.just(when (parameter) {
            is AzureMqttPublish -> azureMqttPublishRepository
            is GooglePublish -> googlePublishRepository
            is MqttPublish -> mqttPublishRepository
            is RestPublish -> restPublishRepository
            else -> throw IllegalArgumentException("Invalid publish type, someone forgot to implement repository in GetRepositoryForPublishUseCase")
        } as PublishRepository<P>)
    }
}