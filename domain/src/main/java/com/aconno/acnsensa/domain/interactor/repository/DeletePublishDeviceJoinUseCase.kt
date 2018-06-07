package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.ifttt.GooglePublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.PublishDeviceJoin
import com.aconno.acnsensa.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.acnsensa.domain.ifttt.RestPublishDeviceJoin
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
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
                else -> throw IllegalArgumentException("Illegal argument provided")
            }
        }
    }
}