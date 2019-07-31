package com.aconno.sensorics.domain.interactor.data

import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithTwoParameters
import io.reactivex.Completable

class StoreTextUseCase(
    private val storeDataUseCase: StoreDataUseCase
) : CompletableUseCaseWithTwoParameters<String, String> {
    override fun execute(parameter1: String, parameter2: String): Completable {
        return storeDataUseCase.execute(parameter1, parameter2.toByteArray())
    }
}