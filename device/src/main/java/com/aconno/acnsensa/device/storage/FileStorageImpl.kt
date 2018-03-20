package com.aconno.acnsensa.device.storage

import android.content.Context
import com.aconno.acnsensa.domain.FileStorage
import com.aconno.acnsensa.domain.model.readings.Reading
import java.io.File

/**
 * @aconno
 */
class FileStorageImpl(private val context: Context) : FileStorage {
    override fun storeReading(reading: Reading, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        file.printWriter().use { out -> out.println(reading.getCsvString()) }
    }
}