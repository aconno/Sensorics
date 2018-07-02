package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.FlowableUseCase
import com.aconno.acnsensa.domain.interactor.type.FlowableUseCaseWithParameter
import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.repository.DeviceRepository
import io.reactivex.Flowable
import io.reactivex.Single

class GetSavedDevicesUseCase(
    private val deviceRepository: DeviceRepository
) : FlowableUseCase<List<Device>> {

    override fun execute(): Flowable<List<Device>> {
        return deviceRepository.getAllDevices()
    }
}