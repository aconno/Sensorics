package com.aconno.sensorics.device.usecase

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Path

interface RetrofitUseCaseApi {

    @GET("/cgi-bin/htmllist.cgi")
    fun getHtmlListing(): Call<String>

    @GET("/resources/sensorics_files/html_files/{sensorName}.html")
    fun getHtml(@Path("sensorName") sensorName: String): Call<String>

    @HEAD("/resources/sensorics_files/html_files/{sensorName}.html")
    fun getLastModifiedDate(@Path("sensorName") sensorName: String): Call<Void>
}