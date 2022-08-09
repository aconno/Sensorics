package com.aconno.sensorics.data.repository.sync

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.domain.model.Sync
import com.aconno.sensorics.domain.repository.SyncRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

class SyncRepositoryTest {
    private lateinit var database: SensoricsDatabase
    private lateinit var syncRepository: SyncRepository

    @Before @Throws(IOException::class)
    fun before() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        database = Room.inMemoryDatabaseBuilder(context, SensoricsDatabase::class.java).build()
        syncRepository = SyncRepositoryImpl(database.syncDao())
    }

    @Test @Throws(IOException::class)
    fun test() {

        val fakeSyncs = listOf(
            Sync("0", "00:00:00:00:00:00", "CF01", System.currentTimeMillis()),
            Sync("0", "00:00:00:00:00:01", "CF01", System.currentTimeMillis()),
            Sync("1", "00:00:00:00:00:02", "CF01", System.currentTimeMillis())
        )

        fakeSyncs.forEach {
            syncRepository.save(it)
        }

        assertEquals(
            fakeSyncs[1].macAddress,
            syncRepository.getSync(fakeSyncs.first().publisherUniqueId).last().macAddress
        )
    }

    @After @Throws(IOException::class)
    fun after() {
        database.close()
    }

}