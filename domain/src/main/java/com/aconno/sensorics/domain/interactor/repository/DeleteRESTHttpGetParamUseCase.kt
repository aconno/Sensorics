package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RESTHttpGetParam
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class DeleteRESTHttpGetParamUseCase(
    private val restPublishRepository: RESTPublishRepository
) : CompletableUseCaseWithParameter<RESTHttpGetParam> {

    override fun execute(parameter: RESTHttpGetParam): Completable {
        return Completable.fromAction {
            restPublishRepository.deleteRESTHttpGetParam(parameter)
        }
    }
}
