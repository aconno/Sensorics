package com.aconno.acnsensa.domain.interactor.filter

import com.aconno.acnsensa.domain.interactor.type.MaybeUseCaseWithTwoParameters
import com.aconno.acnsensa.domain.model.SensorReading
import io.reactivex.Maybe

class FilterReadingsByMacAddressUseCase :
    MaybeUseCaseWithTwoParameters<List<SensorReading>, List<SensorReading>, String> {

    override fun execute(
        sensorReadings: List<SensorReading>,
        macAddress: String
    ): Maybe<List<SensorReading>> {
        return if (sensorReadings[0].device.macAddress == macAddress) {
            Maybe.just(sensorReadings)
        } else {
            Maybe.empty()
        }
    }
}