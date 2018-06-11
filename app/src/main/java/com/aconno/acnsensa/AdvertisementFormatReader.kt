package com.aconno.acnsensa

import android.content.Context
import android.content.res.AssetManager
import com.aconno.acnsensa.model.GenericFormatModel
import com.google.gson.Gson
import io.reactivex.Flowable
import java.io.IOException

class AdvertisementFormatReader {

    companion object {
        private const val PATH = "beacons"
    }

    fun readFlowable(context: Context): Flowable<List<GenericFormatModel>> {
        return Flowable.fromCallable {
            val assets = context.assets

            val results = mutableListOf<GenericFormatModel>()
            val files = assets.list(PATH)

            files.forEach {
                try {
                    val fileData = getFileData(assets, "$PATH/$it")
                    val advertisementFormat = toAdvertisementFormat(fileData)
                    results.add(advertisementFormat)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            results
        }
    }

    private fun toAdvertisementFormat(data: String): GenericFormatModel {
        return Gson().fromJson(data, GenericFormatModel::class.java)
    }

    private fun getFileData(assetManager: AssetManager, fileName: String): String {
        val inputStream = assetManager.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer)
    }
}