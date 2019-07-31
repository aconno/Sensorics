package com.aconno.sensorics.domain.interactor.data

import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithParameter
import io.reactivex.Single
import java.io.File
import java.io.IOException

class StoreTempDataUseCase(
    private val fileStorage: FileStorage
) : SingleUseCaseWithParameter<Pair<String, File>, ByteArray> {
    override fun execute(parameter: ByteArray): Single<Pair<String, File>> {
        return try {
            Single.just(fileStorage.storeTempData(parameter))
        } catch (e: IllegalArgumentException) {
            Single.error(e)
        } catch (e: IOException) {
            Single.error(e)
        } catch (e: SecurityException) {
            Single.error(e)
        }
    }
}