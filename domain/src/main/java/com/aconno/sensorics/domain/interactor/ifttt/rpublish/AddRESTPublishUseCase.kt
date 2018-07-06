package com.aconno.sensorics.domain.interactor.ifttt.rpublish

import com.aconno.sensorics.domain.ifttt.RESTPublish
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class AddRESTPublishUseCase(private val restPublishRepository: RESTPublishRepository) :
    SingleUseCaseWithParameter<Long, RESTPublish> {
    override fun execute(parameter: RESTPublish): Single<Long> {
        return Single.fromCallable {
            restPublishRepository.addRESTPublish(parameter)
        }
    }
}