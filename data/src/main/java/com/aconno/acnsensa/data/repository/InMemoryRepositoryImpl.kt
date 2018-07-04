package com.aconno.acnsensa.data.repository

import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject
import java.util.concurrent.CopyOnWriteArrayList

class InMemoryRepositoryImpl : InMemoryRepository {

    private val buffers = hashMapOf<String, CopyOnWriteArrayList<Reading>>()
    private val subjects = hashMapOf<String, ReplaySubject<List<Reading>>>()

    override fun addReading(reading: Reading) {
        if (!buffers.containsKey(getKey(reading))) {
            buffers[getKey(reading)] = CopyOnWriteArrayList()
        }
        val buffer = buffers[getKey(reading)]
        buffer?.let { addToBuffer(reading, buffer) }

        if (!subjects.containsKey(getKey(reading))) {
            subjects[getKey(reading)] = ReplaySubject.create(1)
        }
        val subject = subjects[getKey(reading)]
        subject?.let { buffer?.let { subject.onNext(buffer) } }
    }

    private fun addToBuffer(reading: Reading, readings: MutableList<Reading>) {
        if (readings.size >= BUFFER_SIZE) {
            readings.removeAt(0)
        }
        readings.add(reading)
    }

    override fun getReadingsFor(macAddress: String, type: String): Observable<List<Reading>> {
        return subjects[getKey(macAddress, type)] ?: Observable.empty()
    }

    override fun deleteAllReadings() {
        buffers.values.forEach { it.clear() }
    }

    private fun getKey(reading: Reading): String {
        return "${reading.device.macAddress}${reading.name}"
    }

    private fun getKey(macAddress: String, readingName: String): String {
        return "$macAddress$readingName"
    }

    companion object {

        private const val BUFFER_SIZE = 500
    }
}