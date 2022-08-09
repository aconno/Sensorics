package com.aconno.sensorics.domain.interactor.ifttt

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetRepositoryForPublishUseCase
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class UpdateAnyPublishUseCase(
    private val getRepositoryForPublishUseCase: GetRepositoryForPublishUseCase
) : SingleUseCaseWithParameter<Unit, BasePublish> {
    override fun execute(parameter: BasePublish): Single<Unit> {
        return getRepositoryForPublishUseCase.execute(parameter).map { repository ->
            repository.updatePublish(parameter)
        }
    }
}