package com.aconno.sensorics.domain.interactor.data

import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single
import java.io.File

class StoreTempTextUseCase(
    private val storeTempDataUseCase: StoreTempDataUseCase
) : SingleUseCaseWithParameter<Pair<String, File>, String> {
    override fun execute(parameter: String): Single<Pair<String, File>> {
        return storeTempDataUseCase.execute(parameter.toByteArray())
    }
}