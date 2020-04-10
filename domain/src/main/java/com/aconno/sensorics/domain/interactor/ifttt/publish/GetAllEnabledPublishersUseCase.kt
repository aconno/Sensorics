package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.publish.PublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

class GetAllEnabledPublishersUseCase(
    private val repositories: List<PublishRepository<out BasePublish>>
) : SingleUseCase<List<BasePublish>> {

    override fun execute(): Single<List<BasePublish>> {
        return Single.merge(repositories.map {
            it.allEnabled
        }).singleOrError()
    }
}