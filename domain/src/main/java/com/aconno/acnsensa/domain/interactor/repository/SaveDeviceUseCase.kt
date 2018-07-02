package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.repository.DeviceRepository
import io.reactivex.Completable

class SaveDeviceUseCase(
    private val deviceRepository: DeviceRepository
) : CompletableUseCaseWithParameter<Device> {

    override fun execute(parameter: Device): Completable {
        return deviceRepository.insertDevice(parameter)
    }
}