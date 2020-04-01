package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.publish.PublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import io.reactivex.Single

open class GetAllEnabledPublishUseCase<P>(
    private val repository: PublishRepository<P>
) : SingleUseCase<List<P>> where P : BasePublish {
    override fun execute(): Single<List<P>> {
        return repository.allEnabled
    }
}