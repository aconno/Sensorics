package com.aconno.acnsensa.domain.repository

import com.aconno.acnsensa.domain.model.Reading
import io.reactivex.Observable

interface InMemoryRepository {

    fun addReading(reading: Reading)

    fun getReadingsFor(macAddress: String, type: String): Observable<List<Reading>>

    fun deleteAllReadings()
}