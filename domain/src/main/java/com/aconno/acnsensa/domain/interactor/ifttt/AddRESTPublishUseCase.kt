package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class AddRESTPublishUseCase(private val restPublishRepository: RESTPublishRepository) :
    SingleUseCaseWithParameter<Long, RESTPublish> {
    override fun execute(parameter: RESTPublish): Single<Long> {
        return Single.fromCallable {
            restPublishRepository.addRESTPublish(parameter)
        }
    }
}