package com.aconno.sensorics.domain.interactor.data

import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithTwoParameters
import io.reactivex.Completable
import io.reactivex.Single
import java.io.FileNotFoundException
import java.io.IOException

class StoreDataUseCase(
    private val fileStorage: FileStorage
) : CompletableUseCaseWithTwoParameters<String, ByteArray> {
    override fun execute(parameter1: String, parameter2: ByteArray): Completable {
        return try {
            fileStorage.storeData(parameter1, parameter2)
            Completable.complete()
        } catch (e: IOException) {
            Completable.error(e)
        } catch (e: FileNotFoundException) {
            Completable.error(e)
        }
    }
}