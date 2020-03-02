package com.aconno.sensorics.domain.interactor.ifttt.restpublish

import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.publish.RestPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledRestPublishUseCase(
    private val restPublishRepository: RestPublishRepository
) : SingleUseCase<List<RestPublish>> {
    override fun execute(): Single<List<RestPublish>> {
        return restPublishRepository.allEnabled
    }
}