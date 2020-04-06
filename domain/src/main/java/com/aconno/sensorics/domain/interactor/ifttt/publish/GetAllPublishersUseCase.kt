package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.publish.PublishRepository
import com.aconno.sensorics.domain.interactor.type.FlowableUseCase
import io.reactivex.Flowable

class GetAllPublishersUseCase(
    private val repositories: List<PublishRepository<out BasePublish>>
) : FlowableUseCase<BasePublish> {
    override fun execute(): Flowable<BasePublish> {
        return Flowable.merge(repositories.map {
            it.all.toFlowable().flatMapIterable { it }
        })
    }
}