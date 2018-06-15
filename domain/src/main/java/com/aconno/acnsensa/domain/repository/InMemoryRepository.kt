package com.aconno.acnsensa.domain.repository

import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ReadingType

interface InMemoryRepository {

    fun addReading(reading: Reading)

    fun getReadingsFor(type: ReadingType): List<Reading>

    fun deleteAllReadings()
}