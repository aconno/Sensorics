package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.MaybeUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceRepository
import io.reactivex.Maybe

class GetSavedDevicesMaybeUseCase(
    private val deviceRepository: DeviceRepository
) : MaybeUseCase<List<Device>> {

    override fun execute(): Maybe<List<Device>> {
        return deviceRepository.getAllDevicesMaybe()
    }
}