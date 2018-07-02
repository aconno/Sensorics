package com.aconno.acnsensa

import android.content.Context
import android.content.res.AssetManager
import com.aconno.acnsensa.model.GenericFormatModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Flowable
import java.io.IOException

class AdvertisementFormatReader {

    companion object {
        private const val PATH = "formats"
    }

    private val gson: Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

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
        return gson.fromJson(data, GenericFormatModel::class.java)
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