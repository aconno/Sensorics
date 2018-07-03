package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.CopyOnWriteArrayList

class InMemoryRepositoryImpl : InMemoryRepository {

    private val buffers = hashMapOf<String, CopyOnWriteArrayList<Reading>>()
    private val subjects = hashMapOf<String, PublishSubject<List<Reading>>>()

    override fun addReading(reading: Reading) {
        if (!buffers.containsKey(reading.type)) {
            buffers[reading.type] = CopyOnWriteArrayList()
        }
        val buffer = buffers[reading.type]
        buffer?.let { addToBuffer(reading, buffer) }

        if (!subjects.containsKey(reading.type)) {
            subjects[reading.type] = PublishSubject.create()
        }
        val subject = subjects[reading.type]
        subject?.let { buffer?.let { subject.onNext(buffer) } }
    }

    private fun addToBuffer(reading: Reading, readings: MutableList<Reading>) {
        if (readings.size >= BUFFER_SIZE) {
            readings.removeAt(0)
        }
        readings.add(reading)
    }

    override fun getReadingsFor(type: String): Observable<List<Reading>> {
        return subjects[type] ?: Observable.empty()
    }

    override fun deleteAllReadings() {
        buffers.values.forEach { it.clear() }
    }

    companion object {

        private const val BUFFER_SIZE = 500
    }
}