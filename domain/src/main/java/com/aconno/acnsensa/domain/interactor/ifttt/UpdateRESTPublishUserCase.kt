package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class UpdateRESTPublishUserCase(
    private val restPublishRepository: RESTPublishRepository
) : CompletableUseCaseWithParameter<RESTPublish> {

    override fun execute(parameter: RESTPublish): Completable {
        return Completable.fromAction {
            restPublishRepository.updateRESTPublish(parameter)
        }
    }
}