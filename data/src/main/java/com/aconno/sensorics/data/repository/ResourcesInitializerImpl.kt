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
    override fun init() = copyAssetsIfNotInitialized()

    @Throws(IOException::class)
    private fun copyAssetsIfNotInitialized() {
        val cacheResourcesFolder = File(cacheFolderPath, CACHE_SUBFOLDER)
        val resourcesAlreadyInitialized = cacheResourcesFolder.exists()
        if (!resourcesAlreadyInitialized) {
            cacheResourcesFolder.mkdir()
            copyAssetPathToCache(ASSETS_SUBFOLDER)
        }
    }

    @Throws(IOException::class)
    private fun copyAssetPathToCache(assetPath: String) {
        assets.list(assetPath)?.let { elementsInPath ->
            val assetIsFile = elementsInPath.isEmpty()
            if (assetIsFile) {
                copyAssetToFile(assetPath)
            } else {
                copyAssetFolderContentToCache(elementsInPath, assetPath)
            }
        }
    }

    @Throws(IOException::class)
    private fun copyAssetFolderContentToCache(folderContent: Array<String>, pathToFolder: String) {
        folderContent.forEach { pathRelativeToFolder ->
            copyAssetPathToCache("$pathToFolder/$pathRelativeToFolder")
        }
    }

    @Throws(IOException::class)
    private fun copyAssetToFile(assetFilePath: String) {
        val relativeCacheFilePath = assetFilePath.replaceFirst(ASSETS_SUBFOLDER, CACHE_SUBFOLDER)
        val cacheFileToBeWritten = File(cacheFolderPath, relativeCacheFilePath)

        createParentFolders(cacheFileToBeWritten)
        copyAssetToFile(assetFilePath, cacheFileToBeWritten)
    }

    @Throws(IOException::class)
    private fun copyAssetToFile(assetFilePath: String, cacheFileToBeWritten: File) {
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