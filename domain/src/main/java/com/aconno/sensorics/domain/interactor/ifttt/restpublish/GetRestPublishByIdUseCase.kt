package com.aconno.sensorics.domain.interactor.ifttt.restpublish

import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Maybe
import io.reactivex.Single

class GetRestPublishByIdUseCase(
    private val restPublishRepository: RestPublishRepository
) : MaybeUseCaseWithParameter<RestPublish, Long> {
    override fun execute(parameter: Long): Maybe<RestPublish> {
        return restPublishRepository.getRESTPublishById(parameter)
    }
}