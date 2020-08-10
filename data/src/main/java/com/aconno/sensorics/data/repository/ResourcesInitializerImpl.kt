package com.aconno.sensorics.data.repository

import android.content.res.AssetManager
import com.aconno.sensorics.domain.ResourcesInitializer
import java.io.File
import java.io.IOException

class ResourcesInitializerImpl(
    cacheDir: File,
    private val assets: AssetManager
) : ResourcesInitializer {
    private val cacheFolderPath = cacheDir.absolutePath

    companion object {
        const val ASSETS_SUBFOLDER = "resources"
        const val CACHE_SUBFOLDER = "sensorics"
    }

    @Throws(IOException::class)
    override fun init() {
        val cacheResourcesFolder = File(cacheFolderPath, CACHE_SUBFOLDER)

        if (!cacheResourcesFolder.exists()) {
            cacheResourcesFolder.mkdir()
            copyAssetPathToCache(ASSETS_SUBFOLDER)
        }
    }

    @Throws(IOException::class)
    private fun copyAssetPathToCache(relativeAssetPath: String) {
        assets.list(relativeAssetPath)?.let { elementsInPath ->
            val isFile = elementsInPath.isEmpty()
            if (isFile) {
                copyAssetFileToCache(relativeAssetPath)
            } else {
                elementsInPath.forEach { subElement ->
                    copyAssetPathToCache("$relativeAssetPath/$subElement")
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun copyAssetFileToCache(assetFilePath: String) {
        val relativeCacheFilePath = assetFilePath.replaceFirst(ASSETS_SUBFOLDER, CACHE_SUBFOLDER)
        val cacheFileToBeWritten = File(cacheFolderPath, relativeCacheFilePath)

        createParentFolders(cacheFileToBeWritten)

        assets.open(assetFilePath).use { assetInputStream ->
            cacheFileToBeWritten.outputStream().use {
                assetInputStream.copyTo(it)
            }
        }
    }

    private fun createParentFolders(forFile: File) {
        forFile.parentFile?.takeIf { !it.exists() }?.mkdirs()
    }

}