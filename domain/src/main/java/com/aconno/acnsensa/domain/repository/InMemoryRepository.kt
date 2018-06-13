package com.aconno.acnsensa.domain.repository

import com.aconno.acnsensa.domain.interactor.filter.Reading
import com.aconno.acnsensa.domain.interactor.filter.ReadingType

interface InMemoryRepository {

    fun addReading(reading: Reading)

    fun getReadingsFor(type: ReadingType): List<Reading>

    fun deleteAllReadings()
}