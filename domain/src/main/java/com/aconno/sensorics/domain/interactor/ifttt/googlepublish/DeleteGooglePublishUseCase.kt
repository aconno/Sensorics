package com.aconno.sensorics.domain.interactor.ifttt.googlepublish

import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteGooglePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository
) : CompletableUseCaseWithParameter<GooglePublish> {
    override fun execute(parameter: GooglePublish): Completable {
        return Completable.fromAction {
            googlePublishRepository.deleteGooglePublish(parameter)
        }
    }
}