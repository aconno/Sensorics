package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.model.DeviceGroup
import com.aconno.sensorics.domain.repository.DeviceGroupRepository
import io.reactivex.Completable

class UpdateDeviceGroupUseCase(
    private val deviceGroupRepository: DeviceGroupRepository
) : CompletableUseCaseWithParameter<DeviceGroup> {

    override fun execute(parameter: DeviceGroup): Completable {
        return deviceGroupRepository.updateDeviceGroup(parameter)
    }
}
