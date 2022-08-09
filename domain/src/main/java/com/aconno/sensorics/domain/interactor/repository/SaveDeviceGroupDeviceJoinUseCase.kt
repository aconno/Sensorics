package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.model.DeviceGroupDeviceJoin
import com.aconno.sensorics.domain.repository.DeviceGroupDeviceJoinRepository
import io.reactivex.Completable

class SaveDeviceGroupDeviceJoinUseCase(
    private val deviceGroupDeviceJoinRepository: DeviceGroupDeviceJoinRepository
) : CompletableUseCaseWithParameter<DeviceGroupDeviceJoin> {

    override fun execute(parameter: DeviceGroupDeviceJoin): Completable {
        return Completable.fromAction {
            deviceGroupDeviceJoinRepository.addDeviceGroupDeviceJoin(parameter)
        }
    }
}