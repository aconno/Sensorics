package com.aconno.acnsensa.domain.interactor

import com.aconno.acnsensa.domain.FileStorage
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Reading
import io.reactivex.Completable

class LogReadingUseCase(
    private val fileStorage: FileStorage
) : CompletableUseCaseWithParameter<List<Reading>> {

    override fun execute(parameter: List<Reading>): Completable {
        for (reading in parameter) {
            logReading(reading)
        }
        return Completable.complete()
    }

    private fun logReading(reading: Reading) {
        val fileName = "${reading.name.toLowerCase()}.csv"
        fileStorage.storeReading(reading, fileName)
    }
}