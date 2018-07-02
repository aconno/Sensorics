package com.aconno.acnsensa.domain.repository

import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ReadingType
import io.reactivex.Observable

interface InMemoryRepository {

    fun addReading(reading: Reading)

    fun getReadingsFor(type: ReadingType): Observable<List<Reading>>

    fun deleteAllReadings()
}