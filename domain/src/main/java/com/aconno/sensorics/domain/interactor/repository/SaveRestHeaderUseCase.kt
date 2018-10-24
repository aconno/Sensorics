package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RestHeader
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class SaveRestHeaderUseCase(
    private val restPublishRepository: RestPublishRepository
) : CompletableUseCaseWithParameter<List<RestHeader>> {

    override fun execute(parameter: List<RestHeader>): Completable {
        return Completable.fromAction {
            restPublishRepository.addRESTHeader(parameter)
        }
    }
}
