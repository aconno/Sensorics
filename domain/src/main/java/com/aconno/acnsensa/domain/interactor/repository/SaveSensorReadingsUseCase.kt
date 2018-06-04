package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Completable

class SaveSensorReadingsUseCase(private val readingsRepository: InMemoryRepository) :
    CompletableUseCaseWithParameter<List<SensorReading>> {

    override fun execute(parameter: List<SensorReading>): Completable {
        for (sensorReading in parameter) {
            readingsRepository.addSensorReading(sensorReading)
        }
        return Completable.complete()
    }
}
