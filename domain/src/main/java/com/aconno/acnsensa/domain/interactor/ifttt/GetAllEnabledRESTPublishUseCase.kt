package com.aconno.acnsensa.domain.interactor.ifttt

import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.ifttt.RESTPublishRepository
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledRESTPublishUseCase(
    private val restPublishRepository: RESTPublishRepository
) : SingleUseCase<List<BasePublish>> {
    override fun execute(): Single<List<BasePublish>> {
        return restPublishRepository.getAllEnabledRESTPublish()
    }
}