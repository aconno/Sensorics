package com.aconno.acnsensa

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.aconno.acnsensa.domain.format.GenericFormat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers.hasItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.aconno.acnsensa", appContext.packageName)
    }

    @Test
    fun getAdvertisementsFromAssets() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("com.aconno.acnsensa", appContext.packageName)

        val reader = AdvertisementFormatReader()
        val readFlowable = reader.readFlowable(appContext)


        val test = readFlowable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .test()

        test.awaitTerminalEvent()

        test.assertComplete()
        test.assertNoErrors()
        assert(test.values().isNotEmpty())
    }
}
