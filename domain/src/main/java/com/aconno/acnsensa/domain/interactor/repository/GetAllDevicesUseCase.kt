package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.repository.DeviceRepository
import io.reactivex.Single

class GetAllDevicesUseCase(
    private val deviceRepository: DeviceRepository
) : SingleUseCase<List<Device>> {

    override fun execute(): Single<List<Device>> {
        return deviceRepository.getAllDevices()
    }
}