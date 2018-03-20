package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.readings.Reading
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Completable

/**
 * @author aconno
 */
class RecordSensorValuesUseCase(private val readingsRepository: InMemoryRepository) :
    CompletableUseCaseWithParameter<List<Reading>> {

    override fun execute(parameter: List<Reading>): Completable {
        for (reading in parameter) {
            readingsRepository.addReading(reading)
        }

        return Completable.complete()
    }
}
