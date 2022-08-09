package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.PublishDeviceJoin
import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeletePublishDeviceJoinUseCase(
    private val publishDeviceJoinRepository: PublishDeviceJoinRepository
) : CompletableUseCaseWithParameter<PublishDeviceJoin> {

    override fun execute(parameter: PublishDeviceJoin): Completable {
        return Completable.fromAction {
            publishDeviceJoinRepository.deletePublishDeviceJoin(parameter)
        }
    }
}