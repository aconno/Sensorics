package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Device
import io.reactivex.Maybe

class GetDevicesThatConnectedWithRESTPublishUseCase(
    private val publishDeviceJoinRepository: PublishDeviceJoinRepository
) : MaybeUseCaseWithParameter<List<Device>, Long> {

    override fun execute(parameter: Long): Maybe<List<Device>> {
        return publishDeviceJoinRepository.getDevicesThatConnectedWithRestPublish(parameter)

    }
}