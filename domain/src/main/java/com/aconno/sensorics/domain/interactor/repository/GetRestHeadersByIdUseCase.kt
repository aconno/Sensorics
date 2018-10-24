package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.RestHeader
import com.aconno.sensorics.domain.ifttt.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetRestHeadersByIdUseCase(
    private val restPublishRepository: RestPublishRepository
) : MaybeUseCaseWithParameter<List<RestHeader>, Long> {

    override fun execute(parameter: Long): Maybe<List<RestHeader>> {
        return restPublishRepository.getHeadersByRESTPublishId(parameter)
    }
}