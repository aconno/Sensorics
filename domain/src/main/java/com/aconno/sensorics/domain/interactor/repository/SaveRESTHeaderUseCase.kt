package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RESTHeader
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class SaveRESTHeaderUseCase(
    private val restPublishRepository: RESTPublishRepository
) : CompletableUseCaseWithParameter<List<RESTHeader>> {

    override fun execute(parameter: List<RESTHeader>): Completable {
        return Completable.fromAction {
            restPublishRepository.addRESTHeader(parameter)
        }
    }
}
