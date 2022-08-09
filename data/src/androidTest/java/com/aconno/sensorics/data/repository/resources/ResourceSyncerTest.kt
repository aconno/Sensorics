package com.aconno.sensorics.data.repository.resources

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.aconno.sensorics.data.api.ResourcesApi
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ResourceSyncerTest {
    @get:Rule
    val tempFolder: TemporaryFolder = TemporaryFolder()

    private lateinit var resourceSyncerImpl: ResourceSyncerImpl
    private lateinit var savedFile: File
    private lateinit var context: Context

    @Before
    fun setUp() {
        val rApi = provideResourcesApi()

        val cacheDir = tempFolder.newFolder()

        context = InstrumentationRegistry.getInstrumentation().targetContext

        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp.edit().putLong(ResourceSyncerImpl.LATEST_VERSION, LATEST_VERSION)
        resourceSyncerImpl = ResourceSyncerImpl(
            cacheDir,
            rApi,
            sp
        )

        savedFile = File(cacheDir.absolutePath + "/sensorics/configs/0201.config")
    }

    @Test
    fun sync() {
        resourceSyncerImpl.sync()

        // AconnoAdvertisementConfigs.ACN_BEACON needs to contain latest beacon config
        assertEquals(
            AconnoAdvertisementConfigs.ACN_BEACON,
            savedFile.readText().replace("\t", "    ")
        )
    }

    @After
    fun after() {
        context.deleteSharedPreferences(PREFS_NAME)
    }

    private fun provideResourcesApi(): ResourcesApi {
        // mocking classes in androidTest in Kotlin is not possible without changing tested classes
        // so real ResourcesApi is provided here.
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        return ResourcesApi(Gson(), okHttpClient)
    }

    companion object {
        private const val LATEST_VERSION = 1557484716L
        private const val PREFS_NAME = "FakePrefs"
    }
}