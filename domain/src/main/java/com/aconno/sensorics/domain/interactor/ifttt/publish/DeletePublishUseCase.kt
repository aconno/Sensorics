package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.publish.PublishRepository
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

open class DeletePublishUseCase<P>(
    private val repository: PublishRepository<P>
) : CompletableUseCaseWithParameter<P> where P : BasePublish {
    override fun execute(parameter: P): Completable {
        return Completable.fromCallable {
            repository.deletePublish(parameter)
        }
    }
}