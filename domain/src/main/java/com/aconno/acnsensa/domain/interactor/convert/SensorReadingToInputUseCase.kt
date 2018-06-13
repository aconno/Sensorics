package com.aconno.acnsensa.domain.interactor.convert

import com.aconno.acnsensa.domain.ifttt.GeneralInput
import com.aconno.acnsensa.domain.ifttt.Input
import com.aconno.acnsensa.domain.interactor.filter.Reading
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single

class SensorReadingToInputUseCase : SingleUseCaseWithParameter<List<Input>, List<Reading>> {

    override fun execute(parameter: List<Reading>): Single<List<Input>> {
        return Single.just(parameter.map {
            GeneralInput(
                it.device.macAddress,
                it.value.toFloat(),
                it.type,
                it.timestamp
            )
        })
    }
}