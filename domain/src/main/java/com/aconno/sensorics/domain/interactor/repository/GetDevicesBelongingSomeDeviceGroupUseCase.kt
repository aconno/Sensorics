package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.MaybeUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceGroupDeviceJoinRepository
import io.reactivex.Maybe

class GetDevicesBelongingSomeDeviceGroupUseCase(
    private val deviceGroupDeviceJoinRepository: DeviceGroupDeviceJoinRepository
) : MaybeUseCase<List<Device>> {

    override fun execute(): Maybe<List<Device>> {
        return deviceGroupDeviceJoinRepository.getDevices()
    }


}