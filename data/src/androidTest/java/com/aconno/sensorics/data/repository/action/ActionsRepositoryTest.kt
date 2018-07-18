package com.aconno.sensorics.data.repository.action

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.aconno.sensorics.data.repository.SensoricsDatabase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ActionsRepositoryTest {

    private lateinit var database: SensoricsDatabase

    @Before
    fun createDatabase() {
        val context = InstrumentationRegistry.getTargetContext()
        database = Room.inMemoryDatabaseBuilder(context, SensoricsDatabase::class.java).build()
    }

    @Test
    fun addAndDeleteAction() {
        //TODO: Implement
    }

    @After
    fun closeDatabase() {
        database.close()
    }
}