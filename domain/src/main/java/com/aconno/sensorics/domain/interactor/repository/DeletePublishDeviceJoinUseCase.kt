package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeletePublishDeviceJoinUseCase(
    private val publishDeviceJoinRepository: PublishDeviceJoinRepository
) : CompletableUseCaseWithParameter<PublishDeviceJoin> {

    override fun execute(parameter: PublishDeviceJoin): Completable {
        return Completable.fromAction {
            when (parameter) {
                is GooglePublishDeviceJoin -> publishDeviceJoinRepository.deleteGooglePublishDeviceJoin(
                    parameter
                )
                is RestPublishDeviceJoin -> publishDeviceJoinRepository.deleteRestPublishDeviceJoin(
                    parameter
                )
                is MqttPublishDeviceJoin -> publishDeviceJoinRepository.deleteMqttPublishDeviceJoin(
                    parameter
                )
                else -> throw IllegalArgumentException("Illegal argument provided")
            }
        }
    }
}