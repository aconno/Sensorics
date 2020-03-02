package com.aconno.sensorics.domain.interactor.ifttt.googlepublish

import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.publish.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

class GetGooglePublishByIdUseCase(
    private val googlePublishRepository: GooglePublishRepository
) : MaybeUseCaseWithParameter<GooglePublish, Long> {
    override fun execute(parameter: Long): Maybe<GooglePublish> {
        return googlePublishRepository.getPublishById(parameter)
    }
}