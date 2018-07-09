package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RESTHttpGetParam
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class SaveRESTHttpGetParamUseCase(
    private val restPublishRepository: RESTPublishRepository
) : CompletableUseCaseWithParameter<List<RESTHttpGetParam>> {

    override fun execute(parameter: List<RESTHttpGetParam>): Completable {
        return Completable.fromAction {
            restPublishRepository.addHttpGetParams(parameter)
        }
    }
}
