package com.aconno.sensorics.domain.interactor.resources

import io.reactivex.Single

interface GetMainResourceUseCase {

    fun execute(deviceName: String): Single<String>
}