package com.aconno.sensorics.domain.interactor.ifttt.googlepublish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Maybe
import io.reactivex.Single

class GetGooglePublishByIdUseCase(
    private val googlePublishRepository: GooglePublishRepository
) : MaybeUseCaseWithParameter<GooglePublish, Long> {
    override fun execute(parameter: Long): Maybe<GooglePublish> {
        return googlePublishRepository.getGooglePublishById(parameter)
    }
}