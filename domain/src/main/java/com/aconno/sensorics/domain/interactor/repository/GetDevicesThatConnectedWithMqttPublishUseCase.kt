package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.sensorics.domain.model.Device
import io.reactivex.Maybe


class GetDevicesThatConnectedWithMqttPublishUseCase(
    private val publishDeviceJoinRepository: PublishDeviceJoinRepository
) : MaybeUseCaseWithParameter<List<Device>, Long> {
    override fun execute(parameter: Long): Maybe<List<Device>> {
        return publishDeviceJoinRepository.getDevicesThatConnectedWithMqttPublish(parameter)
    }
}