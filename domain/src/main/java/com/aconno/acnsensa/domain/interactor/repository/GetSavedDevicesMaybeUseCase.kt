package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.MaybeUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.repository.DeviceRepository
import io.reactivex.Maybe

class GetSavedDevicesMaybeUseCase(
    private val deviceRepository: DeviceRepository
) : MaybeUseCase<List<Device>> {

    override fun execute(): Maybe<List<Device>> {
        return deviceRepository.getAllDevicesMaybe()
    }
}