package com.aconno.sensorics.domain.interactor.ifttt.restpublish

import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteRestPublishUseCase(
    private val restPublishRepository: RestPublishRepository
) : CompletableUseCaseWithParameter<RestPublish> {
    override fun execute(parameter: RestPublish): Completable {
        return Completable.fromAction {
            restPublishRepository.deleteRESTPublish(parameter)
        }
    }
}