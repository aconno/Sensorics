package com.aconno.sensorics.data.api

import com.aconno.sensorics.data.repository.resources.ResourceDelta
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.InputStream


class ResourcesApi(
    private val gson: Gson,
    private val okHttpClient: OkHttpClient
) {

    fun getResourceVersionDelta(version: Long): ResourceDelta {

        "$SERVER_URL/sensorics/api/getLatestVersion.php".toHttpUrlOrNull()
            ?.let {
                val httpBuilder = it.newBuilder()

                httpBuilder.addQueryParameter("version", version.toString())


                val request = Request.Builder()
                    .url(httpBuilder.build())
                    .build()

                val response = okHttpClient.newCall(request).execute()

                response.body.let {
                    val stringRepresentations = it.string()
                    return gson.fromJson<ResourceDelta>(
                        stringRepresentations,
                        ResourceDelta::class.java
                    )
                }
            }

        throw IllegalArgumentException("Could not get/parse response from server!")
    }

    fun downloadFile(filePath: String): InputStream? {
        val url = "$SERVER_URL$filePath"

        val request = Request.Builder()
            .url(url)
            .build()

        val response = okHttpClient.newCall(request).execute()

        return if (response.isSuccessful) {
            response.body.byteStream()
        } else {
            null
        }
    }

    companion object {
        const val SERVER_URL = "https://aconno.de"
    }

}