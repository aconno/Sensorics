package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.ifttt.RESTHeader
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
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
