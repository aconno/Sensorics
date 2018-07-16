package com.aconno.sensorics.domain.interactor.ifttt.gpublish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledGooglePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository
) : SingleUseCase<List<BasePublish>> {
    override fun execute(): Single<List<BasePublish>> {
        return googlePublishRepository.getAllEnabledGooglePublish()
    }
}