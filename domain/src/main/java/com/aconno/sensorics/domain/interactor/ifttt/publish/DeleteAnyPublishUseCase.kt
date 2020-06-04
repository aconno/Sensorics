package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

open class DeleteAnyPublishUseCase(
    private val getRepositoryForPublishUseCase: GetRepositoryForPublishUseCase
) : CompletableUseCaseWithParameter<BasePublish> {
    override fun execute(parameter: BasePublish): Completable {
        return Completable.fromCallable {
            getRepositoryForPublishUseCase.execute(parameter).map { repository ->
                repository.deletePublish(parameter)
            }.subscribe()
        }
    }
}