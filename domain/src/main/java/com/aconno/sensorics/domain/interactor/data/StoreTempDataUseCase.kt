package com.aconno.sensorics.domain.interactor.data

import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.interactor.type.SingleUseCaseWithTwoParameters
import io.reactivex.Single
import java.io.File
import java.io.IOException

class StoreTempDataUseCase(
    private val fileStorage: FileStorage
) : SingleUseCaseWithTwoParameters<Pair<String, File>, ByteArray, String> {
    override fun execute(parameter1: ByteArray, parameter2 : String): Single<Pair<String, File>> {
        return try {
            Single.just(fileStorage.storeTempData(parameter1,parameter2))
        } catch (e: IllegalArgumentException) {
            Single.error(e)
        } catch (e: IOException) {
            Single.error(e)
        } catch (e: SecurityException) {
            Single.error(e)
        }
    }
}