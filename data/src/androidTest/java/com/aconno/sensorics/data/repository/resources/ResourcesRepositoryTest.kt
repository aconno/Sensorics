package com.aconno.sensorics.data.repository.resources

import com.aconno.sensorics.data.mapper.ConfigFileJsonModelConverter
import com.aconno.sensorics.data.mapper.FormatJsonConverter
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ResourcesRepositoryTest {

    @get:Rule
    val tempFolder: TemporaryFolder = TemporaryFolder()

    private lateinit var resourcesRepositoryImpl: ResourcesRepositoryImpl

    @Before
    fun setUp() {
        resourcesRepositoryImpl = ResourcesRepositoryImpl(
            tempFolder.root,
            Gson(),
            ConfigFileJsonModelConverter(),
            FormatJsonConverter()
        )
    }

    @Test
    fun getConfigsTest() {
        tempFolder.newFolder("sensorics", "configs")
        (1..10).forEach {
            tempFolder.newFile("sensorics/configs/$it.config")
                .writeText(AconnoAdvertisementConfigs.ACN_SENSA_VECTOR)
        }

        val configsList = resourcesRepositoryImpl.getConfigs()

        assertEquals(10, configsList.size)
        assertEquals("CF00", configsList[0].id)
        assertEquals("acnSENSA", configsList[0].name)
        assertEquals("/sensorics/usecase_screens/acnsensa.html", configsList[0].usecaseScreenPath)
        assertEquals("/sensorics/icons/ic_sensa.png", configsList[0].iconPath)
        assertEquals("/sensorics/formats/CF00.json", configsList[0].formatPath)
        assertEquals("/sensorics/device_screens/acnsensa.html", configsList[0].deviceScreenPath)
    }

    @Test
    fun getFormatsTest() {
        tempFolder.newFolder("sensorics", "formats")
        (1..8).forEach {
            tempFolder.newFile("sensorics/formats/$it.json")
                .writeText(AconnoAdvertisementFormats.ACN_SENSA_VECTOR)
        }

        val formatsList = resourcesRepositoryImpl.getFormats()

        assertEquals(8, formatsList.size)
        assertEquals("CF00", formatsList[0].id)
        assertEquals(10, formatsList[0].getFormat().size)
    }

}