package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.sensorics.domain.repository.InMemoryRepository
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
