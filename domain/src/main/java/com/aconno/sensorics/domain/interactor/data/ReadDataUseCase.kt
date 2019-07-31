package com.aconno.sensorics.domain.interactor.data

import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single
import java.io.IOException

class ReadDataUseCase(
    private val fileStorage: FileStorage
) : SingleUseCaseWithParameter<ByteArray, String> {
    override fun execute(parameter: String): Single<ByteArray> {
        return try {
            Single.just(fileStorage.readData(parameter))
        } catch (e: IOException) {
            Single.error(e)
        }
    }
}