package com.aconno.sensorics.data.repository

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.aconno.sensorics.domain.repository.Settings
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SettingsTest {

    private lateinit var settings: Settings
    private lateinit var context: Context

    @Before @Throws(Exception::class)
    fun before() {
        context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        settings = SettingsImpl(sharedPrefs)
    }

    @Test @Throws(Exception::class)
    fun test() {
        val mac = "00:00:00:00:00:${String.format("%02x", (0..255).random())}"
        println("Mac: $mac")

        settings.setClickedDeviceMac(mac).blockingAwait()

        assertEquals(mac, settings.getLastClickedDeviceMac().blockingGet())

        settings.setClickedDeviceMac("00:00:00:00:00:00").blockingAwait()

        assertEquals("00:00:00:00:00:00", settings.getLastClickedDeviceMac().blockingGet())
    }

    @After @Throws(Exception::class)
    fun after() {
        context.deleteSharedPreferences(PREFS_NAME)
    }

    companion object {
        private const val PREFS_NAME = "FakePrefs"
    }
}