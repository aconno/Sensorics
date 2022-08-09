package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.publish.PublishRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import io.reactivex.Maybe

open class GetPublishByIdUseCase<P>(
    private val repository: PublishRepository<P>
) : MaybeUseCaseWithParameter<P, Long> where P : BasePublish {
    override fun execute(parameter: Long): Maybe<P> {
        return repository.getPublishById(parameter)
    }
}