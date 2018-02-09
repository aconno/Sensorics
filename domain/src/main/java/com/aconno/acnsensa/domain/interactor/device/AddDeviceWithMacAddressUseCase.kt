package com.aconno.acnsensa.domain.interactor.device

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.repository.SensorRepository
import io.reactivex.Completable

class AddDeviceWithMacAddressUseCase(private val sensorRepository: SensorRepository) :
    CompletableUseCaseWithParameter<String> {

    override fun execute(macAddress: String): Completable {
        return sensorRepository.addDevice(macAddress)
    }
}