package com.aconno.sensorics.data.api

import com.aconno.sensorics.data.repository.resources.LatestVersionJsonModel
import com.google.gson.Gson
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream


class ResourcesApi(
    private val gson: Gson,
    private val okHttpClient: OkHttpClient
) {

    fun getLatestVersion(version: Long): LatestVersionJsonModel {

        HttpUrl.parse("http://6e4998d8.ngrok.io/Desktop/sensorics/api/getLatestVersion.php")
            ?.let {
                val httpBuilder = it.newBuilder()

                httpBuilder.addQueryParameter("version", version.toString())


                val request = Request.Builder()
                    .url(httpBuilder.build())
                    .build()

                val response = okHttpClient.newCall(request).execute()

                response.body()?.let {
                    val stringRepresentations = it.string()
                    return gson.fromJson<LatestVersionJsonModel>(
                        stringRepresentations,
                        LatestVersionJsonModel::class.java
                    )
                }
            }

        throw IllegalArgumentException("Could not get/parse response from server!")
    }

    fun downloadFile(filePath: String): InputStream {
        val url = "http://6e4998d8.ngrok.io/Desktop/sensorics$filePath"

        val request = Request.Builder()
            .url(url)
            .build()

        val response = okHttpClient.newCall(request).execute()

        response.body()?.let {
            return it.byteStream()
        }


        throw IllegalArgumentException("Could not get response from server!")
    }

}