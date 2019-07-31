package com.aconno.sensorics.device.storage

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.aconno.sensorics.domain.FileStorage
import com.aconno.sensorics.domain.model.Reading
import java.io.*

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

    @Throws(IOException::class)
    override fun storeData(uri: String, data: ByteArray) {
        context.contentResolver.openFileDescriptor(Uri.parse(uri), "w")?.use {
            // use{} automatically closes the stream
            FileOutputStream(it.fileDescriptor).use { fos ->
                try {
                    fos.write(data)
                } catch (e: IOException) {
                    throw e
                }
            }
        }
    }

    @Throws(IOException::class)
    override fun readData(uri: String): ByteArray {
        context.contentResolver.openFileDescriptor(Uri.parse(uri), "r")?.use {
            FileInputStream(it.fileDescriptor).use { fis ->
                return fis.readBytes()
            }
        }
        throw IOException("ContentResolver failed to open FileDescriptor")
    }

    @Throws(IllegalArgumentException::class, IOException::class, SecurityException::class)
    override fun storeTempData(data: ByteArray): Pair<String, File> { //todo storetemptext i vraca pair
        val tempSharedFile = File.createTempFile("backend", ".json", context.cacheDir)
        tempSharedFile.writeBytes(data)
        return FileProvider.getUriForFile(
            context,
            "com.aconno.sensorics.fileprovider",
            tempSharedFile
        ).toString() to tempSharedFile
    }

}