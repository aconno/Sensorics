package com.aconno.sensorics.data.mapper

import com.aconno.sensorics.data.repository.resources.format.FormatJsonModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import junit.framework.Assert.assertEquals
import org.junit.Test

class FormatJsonConverterImplTest {

    private val formatJsonConverter =
        FormatJsonConverter()

    @Test
    fun convert_acnSensaScalarFormat() {
        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        val advertisementFormat =
            formatJsonConverter.toAdvertisementFormat(gson.fromJson(AconnoAdvertisementFormats.ACN_SENSA_SCALAR,FormatJsonModel::class.java))

        assertEquals("CF01", advertisementFormat.id)
        assertEquals("AcnSensa", advertisementFormat.getName())
        assertEquals("ic_sensa", advertisementFormat.getIcon())

        assertEquals(5, advertisementFormat.getFormat().size)
        assertEquals(5, advertisementFormat.getRequiredFormat().size)
    }
}