package com.aconno.sensorics.device.format

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Path

interface RetrofitAdvertisementFormatApi {

    @GET("/cgi-bin/list.cgi")
    fun getFormatsListing(): Call<String>

    @GET("/resources/sensorics_files/{sensorId}/format.json")
    fun getFormat(@Path("sensorId") sensorId: String): Call<String>

    @HEAD("/resources/sensorics_files/{sensorId}/format.json")
    fun getLastModifiedDate(@Path("sensorId") sensorId: String): Call<Void>
}