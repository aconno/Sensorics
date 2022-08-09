package com.aconno.sensorics.domain.interactor.ifttt.publish

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

open class AddAnyPublishUseCase(
    private val getRepositoryForPublishUseCase: GetRepositoryForPublishUseCase
) : SingleUseCaseWithParameter<Long, BasePublish> {
    override fun execute(parameter: BasePublish): Single<Long> {
        return getRepositoryForPublishUseCase.execute(parameter).map { repository ->
            repository.addPublish(parameter)
        }
    }
}