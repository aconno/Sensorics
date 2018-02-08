package com.aconno.acnsensa.domain.interactor.device

import com.aconno.acnsensa.domain.interactor.type.SingleUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.repository.SensorRepository
import io.reactivex.Single

class GetAllDevicesUseCase(private val sensorRepository: SensorRepository) :
    SingleUseCase<List<Device>> {

    override fun execute(): Single<List<Device>> {
        return sensorRepository.getAllDevices()
    }
}