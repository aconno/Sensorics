package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ReadingType
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