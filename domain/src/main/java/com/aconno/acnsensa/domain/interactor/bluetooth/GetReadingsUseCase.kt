package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.Reading
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Single

/**
 * @author aconno
 */
class GetReadingsUseCase(
    private val inMemoryRepository: InMemoryRepository
) : SingleUseCaseWithParameter<List<Reading>, SensorType> {

    override fun execute(parameter: SensorType): Single<List<Reading>> {
        return Single.just(inMemoryRepository.getReadingsFor(parameter))
    }
}