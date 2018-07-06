package com.aconno.sensorics.domain.interactor.ifttt.gpublish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.GooglePublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllGooglePublishUseCase(
    private val googlePublishRepository: GooglePublishRepository
) : SingleUseCase<List<BasePublish>> {
    override fun execute(): Single<List<BasePublish>> {
        return googlePublishRepository.getAllGooglePublish()
    }
}