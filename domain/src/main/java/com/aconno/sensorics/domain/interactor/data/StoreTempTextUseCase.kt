package com.aconno.sensorics.domain.interactor.data

import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithTwoParameters
import io.reactivex.Single
import java.io.File

class StoreTempTextUseCase(
    private val storeTempDataUseCase: StoreTempDataUseCase
) : SingleUseCaseWithTwoParameters<Pair<String, File>, String, String> {
    override fun execute(parameter1: String, parameter2 : String): Single<Pair<String, File>> {
        return storeTempDataUseCase.execute(parameter1.toByteArray(),parameter2)
    }
}