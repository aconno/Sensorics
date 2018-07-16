package com.aconno.sensorics.domain.interactor.ifttt.rpublish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.RESTPublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllRESTPublishUseCase(
    private val restPublishRepository: RESTPublishRepository
) : SingleUseCase<List<BasePublish>> {
    override fun execute(): Single<List<BasePublish>> {
        return restPublishRepository.getAllRESTPublish()
    }
}