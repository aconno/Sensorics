package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.sensorics.domain.model.DeviceGroup
import com.aconno.sensorics.domain.repository.DeviceGroupRepository
import io.reactivex.Single

class SaveDeviceGroupUseCase (
    private val deviceGroupRepository: DeviceGroupRepository
) : SingleUseCaseWithParameter<Long,DeviceGroup> {

    override fun execute(parameter: DeviceGroup): Single<Long> {
        return deviceGroupRepository.insertDeviceGroup(parameter)
    }
}