package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.publish.PublishRepository
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

open class AddPublishUseCase<P>(
    private val repository: PublishRepository<P>
) : SingleUseCaseWithParameter<Long, P> where P : BasePublish {
    override fun execute(parameter: P): Single<Long> {
        return Single.fromCallable {
            repository.addPublish(parameter)
        }
    }
}