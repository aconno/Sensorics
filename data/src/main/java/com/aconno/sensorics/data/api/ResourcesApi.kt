package com.aconno.sensorics.data.api


interface ResourcesApi {

    @GET("/sensorics/api/getLatestVersion.php")
    fun getLatestVersion(@Query("version") version: Long): Call<String>


    @GET("/sensorics/{filePath}")
    fun downloadFile(@Path("filePath") filePath: String): String
}