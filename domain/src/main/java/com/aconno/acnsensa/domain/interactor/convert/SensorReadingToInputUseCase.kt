package com.aconno.acnsensa.domain.interactor.convert

import com.aconno.acnsensa.domain.ifttt.GeneralInput
import com.aconno.acnsensa.domain.ifttt.Input
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.SensorReading
import io.reactivex.Single

class SensorReadingToInputUseCase : SingleUseCaseWithParameter<List<Input>, List<SensorReading>> {

    override fun execute(parameter: List<SensorReading>): Single<List<Input>> {
        return Single.just(parameter.map {
            GeneralInput(
                it.device.macAddress,
                it.value.toFloat(),
                it.sensorType,
                it.timestamp
            )
        })
    }
}