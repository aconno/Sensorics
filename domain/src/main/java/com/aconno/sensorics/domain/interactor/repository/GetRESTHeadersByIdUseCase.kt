package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RESTHeader
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetRESTHeadersByIdUseCase(
    private val restPublishRepository: RESTPublishRepository
) : MaybeUseCaseWithParameter<List<RESTHeader>, Long> {

    override fun execute(parameter: Long): Maybe<List<RESTHeader>> {
        return restPublishRepository.getHeadersByRESTPublishId(parameter)
    }
}