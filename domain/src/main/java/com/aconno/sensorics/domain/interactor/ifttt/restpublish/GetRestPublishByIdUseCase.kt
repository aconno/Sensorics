package com.aconno.sensorics.domain.interactor.ifttt.restpublish

import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetRestPublishByIdUseCase(
    private val restPublishRepository: RestPublishRepository
) : MaybeUseCaseWithParameter<RestPublish, Long> {
    override fun execute(parameter: Long): Maybe<RestPublish> {
        return restPublishRepository.getPublishById(parameter)
    }
}