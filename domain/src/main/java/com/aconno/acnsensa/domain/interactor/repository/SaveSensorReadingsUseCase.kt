package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Completable

class SaveSensorReadingsUseCase(
    private val readingsRepository: InMemoryRepository
) : CompletableUseCaseWithParameter<List<Reading>> {

    override fun execute(parameter: List<Reading>): Completable {
        for (sensorReading in parameter) {
            readingsRepository.addReading(sensorReading)
        }
        return Completable.complete()
    }
}
