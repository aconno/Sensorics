package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.FlowableUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceRepository
import io.reactivex.Flowable

class GetSavedDevicesUseCase(
    private val deviceRepository: DeviceRepository
) : FlowableUseCase<List<Device>> {

    override fun execute(): Flowable<List<Device>> {
        return deviceRepository.getAllDevices()
    }
}