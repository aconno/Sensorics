package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetRestHttpGetParamsByIdUseCase(
    private val restPublishRepository: RestPublishRepository
) : MaybeUseCaseWithParameter<List<RestHttpGetParam>, Long> {

    override fun execute(parameter: Long): Maybe<List<RestHttpGetParam>> {
        return restPublishRepository.getRESTHttpGetParamsByRESTPublishId(parameter)
    }
}