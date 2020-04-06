package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.ifttt.outcome.PublishType
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithTwoParameters
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Maybe

class GetDevicesConnectedWithPublishUseCase(
    private val publishDeviceJoinRepository: PublishDeviceJoinRepository
) : MaybeUseCaseWithTwoParameters<List<Device>, Long, PublishType> {

    override fun execute(firstParameter: Long, secondParameter: PublishType): Maybe<List<Device>> {
        return publishDeviceJoinRepository.getDevicesConnectedWithPublish(firstParameter, secondParameter.type)
    }
}