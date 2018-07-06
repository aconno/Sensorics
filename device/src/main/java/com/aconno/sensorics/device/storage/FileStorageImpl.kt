package com.aconno.sensorics.device.storage

import android.content.Context
import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.model.Reading
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter

class FileStorageImpl(private val context: Context) : FileStorage {

    override fun storeReading(reading: Reading, fileName: String) {
        val file = File(context.getExternalFilesDir(null), fileName)
        val fileOutputStream = FileOutputStream(file, true)
        val printWriter = PrintWriter(fileOutputStream)
        fileOutputStream.use {
            printWriter.use { out ->
                out.appendln(reading.toCsvString())
            }
        }
    }
}