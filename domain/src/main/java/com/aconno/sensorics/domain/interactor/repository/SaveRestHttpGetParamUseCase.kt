package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class SaveRestHttpGetParamUseCase(
    private val restPublishRepository: RestPublishRepository
) : CompletableUseCaseWithParameter<List<RestHttpGetParam>> {

    override fun execute(parameter: List<RestHttpGetParam>): Completable {
        return Completable.fromAction {
            restPublishRepository.addHttpGetParams(parameter)
        }
    }
}
