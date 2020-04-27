package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceRepository
import io.reactivex.Completable

class UpdateDeviceUseCase(
    private val deviceRepository: DeviceRepository
) : CompletableUseCaseWithParameter<Device> {

    override fun execute(parameter: Device): Completable {
        return deviceRepository.updateDevice(parameter)
    }
}