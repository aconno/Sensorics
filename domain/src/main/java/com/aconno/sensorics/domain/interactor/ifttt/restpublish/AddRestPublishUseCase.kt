package com.aconno.sensorics.domain.interactor.ifttt.restpublish

import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class AddRestPublishUseCase(private val restPublishRepository: RestPublishRepository) :
    SingleUseCaseWithParameter<Long, RestPublish> {
    override fun execute(parameter: RestPublish): Single<Long> {
        return Single.fromCallable {
            restPublishRepository.addPublish(parameter)
        }
    }
}