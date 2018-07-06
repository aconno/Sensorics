package com.aconno.sensorics.domain.interactor.convert

import com.aconno.sensorics.domain.ifttt.GeneralInput
import com.aconno.sensorics.domain.ifttt.Input
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class ReadingToInputUseCase : SingleUseCaseWithParameter<List<Input>, List<Reading>> {

    override fun execute(parameter: List<Reading>): Single<List<Input>> {
        return Single.just(parameter.map {
            GeneralInput(
                it.device.macAddress,
                it.value.toFloat(),
                it.name,
                it.timestamp
            )
        })
    }
}