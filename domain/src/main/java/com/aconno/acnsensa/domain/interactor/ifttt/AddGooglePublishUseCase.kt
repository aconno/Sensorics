package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class AddGooglePublishUseCase(private val googlePublishRepository: GooglePublishRepository) :
    SingleUseCaseWithParameter<Long, GooglePublish> {
    override fun execute(parameter: GooglePublish): Single<Long> {
        return Single.fromCallable {
            googlePublishRepository.addGooglePublish(parameter)
        }
    }
}