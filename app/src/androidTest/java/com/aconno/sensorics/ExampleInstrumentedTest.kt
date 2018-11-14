package com.aconno.sensorics

import android.support.test.runner.AndroidJUnit4
import com.udojava.evalex.Expression
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.math.BigDecimal

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
//        val appContext = InstrumentationRegistry.getTargetContext()
//        assertEquals("com.aconno.sensorics", appContext.packageName)


        val asd = 106.9975f

        val before = System.currentTimeMillis()

        for (i in 0..10000) {
            val x = BigDecimal(asd.toString())
            Expression("x * 245 / 32768").with("x", x).eval()
        }

        Timber.tag("HAHA").d("${System.currentTimeMillis() - before}")
        assert(true)
    }
}
