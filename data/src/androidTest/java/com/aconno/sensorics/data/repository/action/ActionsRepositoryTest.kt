package com.aconno.sensorics.data.repository.action

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.aconno.sensorics.data.repository.SensoricsDatabase
import com.aconno.sensorics.data.repository.devices.DeviceDao
import com.aconno.sensorics.data.repository.devices.DeviceMapper
import com.aconno.sensorics.data.repository.devices.DeviceRepositoryImpl
import com.aconno.sensorics.data.repository.devices.DeviceRepositoryTest.Companion.insertedDevices
import com.aconno.sensorics.data.repository.devices.DeviceRepositoryTest.Companion.populateDevicesDatabase
import com.aconno.sensorics.domain.actions.Action
import com.aconno.sensorics.domain.actions.GeneralAction
import com.aconno.sensorics.domain.actions.outcomes.Outcome
import com.aconno.sensorics.domain.ifttt.LimitCondition
import io.reactivex.schedulers.Schedulers
import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.BeforeClass
import org.junit.Test
import java.io.IOException
import java.util.*

class ActionsRepositoryTest {


    @Test
    @Throws(IOException::class)
    fun addAction() {
        populateActionsDatabase(10)

        assertEquals(insertedActions.size, actionDao.getAll().blockingGet().size)
    }

    @Test @Throws(IOException::class)
    fun getActionsByDeviceMacAddress() {
        populateActionsDatabase(10)
        val mac = insertedActions[(1..insertedActions.size).random() - 1].device.macAddress
        val expectedIds = ((Integer.parseInt(mac.takeLast(2), 16)
            )..insertedActions.size step insertedDevices.size).toList()

        val actualIds = ari.getActionsByDeviceMacAddress(mac)
            .blockingGet()
            .map { action -> action.id.toInt() }

        assertTrue(expectedIds.zip(actualIds).all{ it.first == it.second })
        assertEquals(expectedIds.size, actualIds.size)
    }

    @Test @Throws(IOException::class)
    fun getActionById() {
        populateActionsDatabase(12)
        val id = (1..insertedActions.size).random().toLong()

        assertEquals(id, ari.getActionById(id).blockingGet().id)
    }

    @Test @Throws(IOException::class)
    fun getAllActions() {
        populateActionsDatabase(23)

        assertTrue(insertedActions.zip(ari.getAllActions().blockingGet())
            .all{ it.first.id == it.second.id })
    }

    @Test @Throws(IOException::class)
    fun deleteAllActions() {
        populateActionsDatabase(27)
        while(insertedActions.size > 0) {
            ari.deleteAction(insertedActions.pop()).blockingAwait()
        }

        assertTrue(actionDao.getAll().blockingGet().isEmpty())
    }

    companion object {

        private val insertedActions = LinkedList<Action>()

        private lateinit var database: SensoricsDatabase
        private lateinit var deviceDao: DeviceDao
        private lateinit var dri: DeviceRepositoryImpl
        private lateinit var actionDao: ActionDao
        private lateinit var ari: ActionsRepositoryImpl

        @BeforeClass @JvmStatic
        fun createDatabase() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
            database = Room.inMemoryDatabaseBuilder(context, SensoricsDatabase::class.java).build()
            actionDao = database.actionDao()
            ari = ActionsRepositoryImpl(actionDao,ActionMapper())
            deviceDao = database.deviceDao()
            dri = DeviceRepositoryImpl(deviceDao, DeviceMapper())
        }

        fun populateActionsDatabase(
            actionsNum: Int,
            actionsRepositoryImpl: ActionsRepositoryImpl = ari
        ) {
            if(insertedActions.size > 0) return
            
            populateDevicesDatabase(4, dri)
            val fakeDevices = insertedDevices
            val fakeOutcome = Outcome(
                mapOf("fake" to "outcome"),
                Outcome.OUTCOME_TYPE_NOTIFICATION
            )
            val fakeCondition = LimitCondition("readingType", 1, 1.5f)

            (1..actionsNum).forEach {
                insertedActions.add(GeneralAction(
                    it.toLong(),
                    "name",
                    fakeDevices[(it - 1) % fakeDevices.size],
                    fakeCondition,
                    fakeOutcome,
                    false,
                    0,
                    Random().nextInt()
                ))
                actionsRepositoryImpl.addAction(insertedActions.last())
                    .subscribeOn(Schedulers.io())
                    .blockingGet()
            }
        }

        @AfterClass @JvmStatic
        fun closeDatabase() {
            database.close()
        }
    }
}