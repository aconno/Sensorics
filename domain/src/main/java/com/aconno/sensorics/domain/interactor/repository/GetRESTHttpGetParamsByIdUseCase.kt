package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RESTHttpGetParam
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetRESTHttpGetParamsByIdUseCase(
    private val restPublishRepository: RESTPublishRepository
) : MaybeUseCaseWithParameter<List<RESTHttpGetParam>, Long> {

    override fun execute(parameter: Long): Maybe<List<RESTHttpGetParam>> {
        return restPublishRepository.getRESTHttpGetParamsByRESTPublishId(parameter)
    }
}