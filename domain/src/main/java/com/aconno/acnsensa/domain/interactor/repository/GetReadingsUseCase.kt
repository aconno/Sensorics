package com.aconno.acnsensa.domain.interactor.repository

import com.aconno.acnsensa.domain.interactor.type.ObservableUseCaseWithParameter
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ReadingType
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Observable

class GetReadingsUseCase(
    private val inMemoryRepository: InMemoryRepository
) : ObservableUseCaseWithParameter<List<Reading>, ReadingType> {

    override fun execute(parameter: ReadingType): Observable<List<Reading>> {
        return inMemoryRepository.getReadingsFor(parameter)
    }
}