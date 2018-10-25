package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.ifttt.PublishDeviceJoinRepository
import com.aconno.sensorics.domain.model.Device


class GetDevicesThatConnectedWithMqttPublishUseCase(
    private val publishDeviceJoinRepository: PublishDeviceJoinRepository
) {
    fun execute(parameter: Long): List<Device>? {
        return publishDeviceJoinRepository.getDevicesThatConnectedWithMqttPublish(parameter)

    }
}