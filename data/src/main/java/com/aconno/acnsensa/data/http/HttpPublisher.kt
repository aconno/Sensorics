package com.aconno.acnsensa.data.http

import com.aconno.acnsensa.data.mqtt.GoogleCloudDataConverter
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import timber.log.Timber


class HttpPublisher(private val restPublish: RESTPublish) : Publisher {

    private val httpClient: OkHttpClient = OkHttpClient()

    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
//        private const val GET_PARAMETERNAME = "ACNSensa"
    }

    override fun publish(reading: Reading) {
        val messages = GoogleCloudDataConverter.convert(reading)

        Observable.fromIterable(messages)
            .subscribeOn(Schedulers.io())
            .concatMap { it -> getRequestObservable(it) }
            .subscribe {
                Timber.d(it.body().toString())
            }
    }

    private fun getRequestObservable(message: String): Observable<Response> {
        return Observable.fromCallable {
            when {
                restPublish.method == "GET" -> {
//                    val httpBuilder = HttpUrl.parse(restPublish.url)!!.newBuilder()
//                    httpBuilder.addQueryParameter(GET_PARAMETERNAME, message)

                    val urlWithParam = restPublish.url + "/" + message
                    httpClient.newCall(Request.Builder().url(urlWithParam).build()).execute()
                }
                restPublish.method == "POST" -> {
                    val body = RequestBody.create(JSON, message)
                    val request = Request.Builder()
                        .url(restPublish.url)
                        .post(body)
                        .build()
                    httpClient.newCall(request).execute()
                }
                else -> {
                    throw IllegalArgumentException("Illegal Http method please check from getRequestObservable list.")
                }
            }
        }
    }

    override fun closeConnection() {
        //Do-nothing
    }


}