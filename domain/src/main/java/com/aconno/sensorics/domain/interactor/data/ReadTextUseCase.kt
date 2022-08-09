package com.aconno.sensorics.domain.interactor.data

import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single
import java.nio.charset.Charset

class ReadTextUseCase(
    private val readDataUseCase: ReadDataUseCase
) : SingleUseCaseWithParameter<String, String> {
    override fun execute(parameter: String): Single<String> {
        return readDataUseCase.execute(parameter)
            .flatMap {
                Single.just(it.toString(Charset.defaultCharset()))
            }
    }
}