package com.aconno.sensorics.domain.repository

import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Observable

interface InMemoryRepository {

    fun addReading(reading: Reading)

    fun getReadingsFor(macAddress: String, type: String): Observable<List<Reading>>

    fun deleteAllReadings()
}