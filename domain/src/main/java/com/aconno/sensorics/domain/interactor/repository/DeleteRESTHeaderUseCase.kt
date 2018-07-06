package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RESTHeader
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteRESTHeaderUseCase(
    private val restPublishRepository: RESTPublishRepository
) : CompletableUseCaseWithParameter<RESTHeader> {

    override fun execute(parameter: RESTHeader): Completable {
        return Completable.fromAction {
            restPublishRepository.deleteRESTHeader(parameter)
        }
    }
}
