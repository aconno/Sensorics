package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import com.aconno.sensorics.domain.model.DeviceGroup
import com.aconno.sensorics.domain.repository.DeviceGroupRepository
import io.reactivex.Single

class GetSavedDeviceGroupsUseCase(
    private val deviceGroupRepository: DeviceGroupRepository
) : SingleUseCase<List<DeviceGroup>> {

    override fun execute(): Single<List<DeviceGroup>> {
        return deviceGroupRepository.getAllDeviceGroups()
    }
}