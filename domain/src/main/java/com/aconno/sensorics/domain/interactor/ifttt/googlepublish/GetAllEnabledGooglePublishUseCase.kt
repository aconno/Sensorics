package com.aconno.sensorics.domain.interactor.ifttt.googlepublish

import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.publish.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledGooglePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository
) : SingleUseCase<List<GooglePublish>> {
    override fun execute(): Single<List<GooglePublish>> {
        return googlePublishRepository.allEnabled
    }
}