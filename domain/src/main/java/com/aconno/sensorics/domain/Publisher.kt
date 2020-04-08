package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.repository.SyncRepository
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

abstract class Publisher<T>(
    val publish: T,
    val listDevices: List<Device>,
    val syncRepository: SyncRepository
) where T : BasePublish {
    val lastSyncs: MutableMap<Pair<String, String>, Long> = syncRepository.getSync(
            publish.type.type + publish.id
        )
        .map { Pair(it.macAddress, it.advertisementId) to it.lastSyncTimestamp }
        .toMap()
        .toMutableMap()

    val messageQueue: Queue<String> = ConcurrentLinkedQueue()

    abstract fun publish(readings: List<Reading>)

    abstract fun closeConnection()

    abstract fun test(testConnectionCallback: TestConnectionCallback)

    interface TestConnectionCallback {
        fun onConnectionStart()
        fun onConnectionSuccess()
        fun onConnectionFail(exception: Throwable?)
    }

    fun isPublishable(readings: List<Reading>): Boolean {
        val reading = readings.firstOrNull()
        val latestTimestamp =
            lastSyncs[Pair(reading?.device?.macAddress, reading?.advertisementId)] ?: 0

        return System.currentTimeMillis() - latestTimestamp > this.publish.timeMillis
            && reading != null && listDevices.contains(reading.device)
    }
}