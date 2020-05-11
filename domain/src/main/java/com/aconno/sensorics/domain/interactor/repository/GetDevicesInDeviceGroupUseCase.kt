package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.MaybeUseCaseWithParameter
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.repository.DeviceGroupDeviceJoinRepository
import io.reactivex.Maybe

class GetDevicesInDeviceGroupUseCase (
    private val deviceGroupDeviceJoinRepository: DeviceGroupDeviceJoinRepository
) : MaybeUseCaseWithParameter<List<Device>, Long> {

    override fun execute(parameter: Long): Maybe<List<Device>> {
        return deviceGroupDeviceJoinRepository.getDevicesInDeviceGroup(parameter)
    }
}