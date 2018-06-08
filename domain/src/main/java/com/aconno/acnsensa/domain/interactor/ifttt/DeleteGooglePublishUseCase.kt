package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
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