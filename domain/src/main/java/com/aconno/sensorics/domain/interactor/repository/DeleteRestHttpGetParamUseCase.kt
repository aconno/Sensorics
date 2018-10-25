package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteRestHttpGetParamUseCase(
    private val restPublishRepository: RestPublishRepository
) : CompletableUseCaseWithParameter<RestHttpGetParam> {

    override fun execute(parameter: RestHttpGetParam): Completable {
        return Completable.fromAction {
            restPublishRepository.deleteRESTHttpGetParam(parameter)
        }
    }
}
