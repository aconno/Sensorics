package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.ifttt.RESTHeader
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
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
