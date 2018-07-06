package com.aconno.sensorics.domain.interactor.ifttt.gpublish

import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class UpdateGooglePublishUseCase(private val googlePublishRepository: GooglePublishRepository) :
    CompletableUseCaseWithParameter<GooglePublish> {
    override fun execute(parameter: GooglePublish): Completable {
        return Completable.fromAction {
            googlePublishRepository.updateGooglePublish(parameter)
        }
    }
}