package com.aconno.sensorics.domain.interactor.ifttt.gpublish

import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class AddGooglePublishUseCase(private val googlePublishRepository: GooglePublishRepository) :
    SingleUseCaseWithParameter<Long, GooglePublish> {
    override fun execute(parameter: GooglePublish): Single<Long> {
        return Single.fromCallable {
            googlePublishRepository.addGooglePublish(parameter)
        }
    }
}