package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RestHeader
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteRestHeaderUseCase(
    private val restPublishRepository: RestPublishRepository
) : CompletableUseCaseWithParameter<RestHeader> {

    override fun execute(parameter: RestHeader): Completable {
        return Completable.fromAction {
            restPublishRepository.deleteRESTHeader(parameter)
        }
    }
}
