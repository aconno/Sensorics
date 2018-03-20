package com.aconno.acnsensa.domain.interactor.device

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithTwoParameters
import com.aconno.acnsensa.domain.repository.SensorRepository
import io.reactivex.Completable

class AddDeviceWithMacAddressAndNameUseCase(private val sensorRepository: SensorRepository) :
    CompletableUseCaseWithTwoParameters<String, String> {

    override fun execute(macAddress: String, name: String): Completable {
        return sensorRepository.addDevice(macAddress, name)
    }
}