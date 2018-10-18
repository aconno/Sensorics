package com.aconno.sensorics.device.format

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import org.junit.Test
import kotlin.test.assertEquals

class FormatJsonConverterImplTest {

    private val formatJsonConverter =
        FormatJsonConverterImpl(
            GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()
        )

    @Test
    fun convert_acnSensaScalarFormat() {
        val advertisementFormat =
            formatJsonConverter.toAdvertisementFormat(AconnoAdvertisementFormats.ACN_SENSA_SCALAR)

        assertEquals("CF01", advertisementFormat.id)
        assertEquals("AcnSensa", advertisementFormat.getName())
        assertEquals("ic_sensa", advertisementFormat.getIcon())

        assertEquals(5, advertisementFormat.getFormat().size)
        assertEquals(5, advertisementFormat.getRequiredFormat().size)
    }
}