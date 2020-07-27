package com.aconno.sensorics.data.repository

import com.aconno.sensorics.data.repository.devices.DeviceRepositoryTest
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.repository.InMemoryRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryRepositoryTest {
    private val repo: InMemoryRepository = InMemoryRepositoryImpl()

    @Test
    fun addAndGetReadings() {
        val fakeDevices = DeviceRepositoryTest.generateFakeDevicesList(42.toByte())
        val fakeReadings = generateFakeReadingsList(512, fakeDevices)
        fakeReadings.forEach { repo.addReading(it) }
        assertEquals(
            13,
            repo.getReadingsFor("00:00:00:00:00:08", "Temperature")
                .blockingFirst()
                .size
        )
        for(i in 0 until 505) {
            repo.addReading(fakeReadings[7])
        }
        assertEquals(
            500,
            repo.getReadingsFor("00:00:00:00:00:08", "Temperature")
                .blockingFirst()
                .size
        )
    }

    @Test
    fun addAndDeleteReadings() {

        val fakeDevices = DeviceRepositoryTest.generateFakeDevicesList(5.toByte())
        val fakeReadings = generateFakeReadingsList(28, fakeDevices)
        fakeReadings.forEach { repo.addReading(it) }

        assertEquals(
            6,
            repo.getReadingsFor("00:00:00:00:00:03", "Temperature")
                .blockingFirst()
                .size
        )
        repo.deleteAllReadings()
        assertTrue(fakeDevices.all {
            repo.getReadingsFor(it.macAddress, "Temperature").blockingFirst().isEmpty()
        })
    }

    private fun generateFakeReadingsList(
        readingsNum: Int,
        fakeDevices: List<Device>
    ): List<Reading> {
        val returnValue = ArrayList<Reading>()
        for(i in 0 until readingsNum) {
            returnValue.add(Reading(
                timestamp = System.currentTimeMillis(),
                device = fakeDevices[i % fakeDevices.size],
                value = i,
                name = "Temperature",
                rssi = 0,
                advertisementId = "CF01",
                deviceGroup = null
            ))
        }
        return returnValue
    }
}