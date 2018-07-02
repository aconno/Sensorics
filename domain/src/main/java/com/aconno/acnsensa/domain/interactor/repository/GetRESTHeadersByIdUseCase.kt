package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.ifttt.RESTHeader
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetRESTHeadersByIdUseCase(
    private val restPublishRepository: RESTPublishRepository
) : MaybeUseCaseWithParameter<List<RESTHeader>, Long> {

    override fun execute(parameter: Long): Maybe<List<RESTHeader>> {
        return restPublishRepository.getHeadersByRESTPublishId(parameter)
    }
}