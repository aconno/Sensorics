package com.aconno.sensorics.domain.interactor.repository

import com.aconno.sensorics.domain.interactor.type.ObservableUseCaseWithTwoParameters
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.repository.InMemoryRepository
import io.reactivex.Observable

class GetReadingsUseCase(
    private val inMemoryRepository: InMemoryRepository
) : ObservableUseCaseWithTwoParameters<List<Reading>, String, String> {

    override fun execute(
        firstParameter: String,
        secondParameter: String
    ): Observable<List<Reading>> {
        return inMemoryRepository.getReadingsFor(firstParameter, secondParameter)
    }
}