package com.aconno.acnsensa.domain.interactor.bluetooth

import com.aconno.acnsensa.domain.interactor.filter.Reading
import com.aconno.acnsensa.domain.interactor.filter.ReadingType
import com.aconno.acnsensa.domain.interactor.type.SingleUseCaseWithParameter
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Single

class GetReadingsUseCase(
    private val inMemoryRepository: InMemoryRepository
) : SingleUseCaseWithParameter<List<Reading>, ReadingType> {

    override fun execute(parameter: ReadingType): Single<List<Reading>> {
        return Single.just(inMemoryRepository.getReadingsFor(parameter))
    }
}