package com.aconno.sensorics.data.repository

import android.content.res.AssetManager
import androidx.test.platform.app.InstrumentationRegistry
import com.aconno.sensorics.data.repository.ResourcesInitializerImpl.Companion.CACHE_SUBFOLDER
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.streams.toList

class ResourcesInitializerImplTest {
    private lateinit var cacheFolder: File
    private lateinit var assetManager: AssetManager

    @Before
    fun setUp() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        cacheFolder = appContext.cacheDir
        cacheFolder.deleteRecursively()

        assetManager = appContext.assets
    }

    @Test
    fun copyAssetsToCache_whenRootFolderExists_shouldNotCopyAssetsToCache() {
        File(cacheFolder, "$CACHE_SUBFOLDER/").mkdirs()

        val expectedFilesInAssets = listOf(
            "",
            CACHE_SUBFOLDER
        )
        val expectedFilesInCache = expectedFilesInAssets.map { File(cacheFolder, it).absolutePath }

        val resourcesInitializerImpl = ResourcesInitializerImpl(cacheFolder, assetManager)
        resourcesInitializerImpl.init()

        val actualPaths = getFileTree(cacheFolder)
        assertThat(actualPaths).containsExactlyElementsIn(expectedFilesInCache)
    }

    @Test
    fun copyAssetsToCache_shouldCopyAssetsToCache() {
        val expectedFilesInAssets = listOf(
            "",
            CACHE_SUBFOLDER,
            "$CACHE_SUBFOLDER/copyRoot",
            "$CACHE_SUBFOLDER/configs",
            "$CACHE_SUBFOLDER/configs/copyConfigs",
            "$CACHE_SUBFOLDER/double",
            "$CACHE_SUBFOLDER/double/nested",
            "$CACHE_SUBFOLDER/double/nested/file"
        )
        val expectedFilesInCache = expectedFilesInAssets.map { File(cacheFolder, it).absolutePath }

        val resourcesInitializerImpl = ResourcesInitializerImpl(cacheFolder, assetManager)
        resourcesInitializerImpl.init()

        val actualPaths = getFileTree(cacheFolder)
        assertThat(actualPaths).containsExactlyElementsIn(expectedFilesInCache)
    }

    private fun getFileTree(rootFolder: File): List<String> {
        return Files.find(
            rootFolder.toPath(),
            Int.MAX_VALUE,
            { _: Path, _: BasicFileAttributes -> true },
            emptyArray()
        ).toList().map { it.toAbsolutePath().toString() }
    }
}