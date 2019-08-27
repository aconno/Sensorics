package com.aconno.sensorics.data.repository.devices

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.domain.model.Device
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import java.io.IOException
import java.util.*


class DeviceRepositoryTest {

    @Test @Throws(IOException::class)
    fun insertDevice() {
        populateDevicesDatabase(13)

        assertEquals(insertedDevices.size, dri.getAllDevices().blockingFirst().size)
    }

    @Test @Throws(IOException::class)
    fun getAllDevices() {
        populateDevicesDatabase(5)

        assertTrue(dri.getAllDevices().blockingFirst().zip(insertedDevices).all {
            it.first == it.second
        })
    }

    @Test @Throws(IOException::class)
    fun deleteAllDevices() {
        populateDevicesDatabase(27)
        while(insertedDevices.size > 0) {
            dri.deleteDevice(insertedDevices.pop()).blockingAwait()
        }

        assertTrue(deviceDao.getAll().blockingFirst().isEmpty())
    }


    companion object {

        val insertedDevices = LinkedList<Device>()

        private lateinit var database: SensoricsDatabase
        private lateinit var deviceDao: DeviceDao
        private lateinit var dri: DeviceRepositoryImpl

        @BeforeClass @JvmStatic
        fun createDatabase() {
            val context = ApplicationProvider.getApplicationContext<Context>()
            database = Room.inMemoryDatabaseBuilder(context, SensoricsDatabase::class.java).build()
            deviceDao = database.deviceDao()
            dri = DeviceRepositoryImpl(deviceDao, DeviceMapper())
        }

        @AfterClass
        @JvmStatic
        fun closeDatabase() {
            database.close()
        }

        fun populateDevicesDatabase(
            devicesNum: Byte,
            deviceRepositoryImpl: DeviceRepositoryImpl = dri
        ) {
            if(insertedDevices.size != 0) return

            insertedDevices.addAll(generateFakeDevicesList(devicesNum))
            insertedDevices.forEach { deviceRepositoryImpl.insertDevice(it).blockingAwait() }
        }

        fun generateFakeDevicesList(devicesNum: Byte): List<Device> {
            val returnValue = ArrayList<Device>()
            (1..devicesNum).forEach {
                returnValue.add(Device(
                    "device$it",
                    "deviceAlias",
                    "00:00:00:00:00:${String.format("%02x", it)}"))
            }
            return returnValue
        }

    }
}