package com.aconno.sensorics.domain.interactor.time

import com.aconno.sensorics.domain.interactor.type.SingleUseCase
import com.aconno.sensorics.domain.time.TimeProvider
import io.reactivex.Single

class GetLocalTimeOfDayInSecondsUseCase(
    private val timeProvider: TimeProvider
) : SingleUseCase<Int> {

    override fun execute(): Single<Int> {
        return Single.just(timeProvider.getLocalTimeOfDayInSeconds())
    }
}