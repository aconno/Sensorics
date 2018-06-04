package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Single

class GetSensorReadingsUseCase(
    private val inMemoryRepository: InMemoryRepository
) : SingleUseCaseWithParameter<List<SensorReading>, SensorTypeSingle> {

    override fun execute(parameter: SensorTypeSingle): Single<List<SensorReading>> {
        return Single.just(inMemoryRepository.getSensorReadingsFor(parameter))
    }
}