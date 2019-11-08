package com.aconno.sensorics.domain

import com.aconno.sensorics.domain.model.Reading
import java.io.File
import java.io.IOException

interface FileStorage {

    fun storeReading(reading: Reading, fileName: String)

    @Throws(IOException::class)
    fun storeData(uri: String, data: ByteArray)

    @Throws(IOException::class)
    fun readData(uri: String): ByteArray

    @Throws(IllegalArgumentException::class, IOException::class)
    fun storeTempData(data: ByteArray, fileNamePrefix : String = "data"): Pair<String, File>
}