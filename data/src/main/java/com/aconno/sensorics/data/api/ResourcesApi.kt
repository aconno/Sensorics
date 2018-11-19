package com.aconno.sensorics.data.api

import com.aconno.sensorics.data.repository.resources.LatestVersionJsonModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface ResourcesApi {

    @GET("/sensorics/api/getLatestVersion.php")
    fun getLatestVersion(@Query("version") version: Long): Call<LatestVersionJsonModel>


    @GET("/sensorics/{filePath}")
    fun downloadFile(@Path("filePath") filePath: String): String
}