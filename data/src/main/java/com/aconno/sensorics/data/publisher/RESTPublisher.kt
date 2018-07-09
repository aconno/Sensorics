package com.aconno.sensorics.data.publisher

import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.data.converter.ReadingToStringParser
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.BasePublish
import com.aconno.sensorics.domain.ifttt.RESTHeader
import com.aconno.sensorics.domain.ifttt.RESTHttpGetParam
import com.aconno.sensorics.domain.ifttt.RESTPublish
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class RESTPublisher(
    private val restPublish: RESTPublish,
    private val listDevices: List<Device>,
    private val listHeaders: List<RESTHeader>,
    private val listHttpGetParams: List<RESTHttpGetParam>
) : Publisher {

    private val httpClient: OkHttpClient
    private val dataStringConverter: DataStringConverter
    private val readingToStringParser: ReadingToStringParser

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        dataStringConverter = DataStringConverter(restPublish.dataString)
        readingToStringParser = ReadingToStringParser()
    }

    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
    }

    override fun publish(reading: Reading) {
        Timber.tag("Publisher HTTP")
            .d("${restPublish.name} publishes from ${reading.device}")
        getRequestObservable(reading)
            .subscribe {
                Timber.d(it.body().toString())
            }

    }

    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {
        val reading = Reading(
            System.currentTimeMillis(),
            Device("TestDevice", "MA:CA:DD:RE:SS:11"),
            1,
            "Temperature"
        )

        getRequestObservable(reading)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isSuccessful) {
                    testConnectionCallback.onConnectionSuccess()
                } else {
                    testConnectionCallback.onConnectionFail()
                }
            }
    }

    //TODO shorten
    private fun getRequestObservable(message: Reading): Observable<Response> {
        return Observable.fromCallable {
            when {
                restPublish.method == "GET" -> {
                    val httpBuilder = HttpUrl.parse(restPublish.url)!!.newBuilder()

                    listHttpGetParams.forEach {
                        httpBuilder.addQueryParameter(
                            readingToStringParser.convert(
                                message,
                                it.key
                            ), readingToStringParser.convert(
                                message,
                                it.value
                            )
                        )
                    }

                    val builder = Request.Builder()
                    listHeaders.forEach {
                        builder.addHeader(
                            readingToStringParser.convert(
                                message,
                                it.key
                            ), readingToStringParser.convert(
                                message,
                                it.value
                            )
                        )
                    }

                    val request = builder.url(httpBuilder.build()).build();

                    httpClient.newCall(request).execute()
                }
                restPublish.method == "POST" -> {
                    val body = RequestBody.create(
                        JSON,
                        readingToStringParser.convert(message, restPublish.dataString)
                    )

                    val builder = Request.Builder()
                    listHeaders.forEach {
                        builder.addHeader(
                            readingToStringParser.convert(
                                message,
                                it.key
                            ), readingToStringParser.convert(
                                message,
                                it.value
                            )
                        )
                    }

                    val request = builder
                        .url(restPublish.url)
                        .post(body)
                        .build()
                    httpClient.newCall(request).execute()
                }
                restPublish.method == "PUT" -> {
                    val body = RequestBody.create(
                        JSON,
                        readingToStringParser.convert(message, restPublish.dataString)
                    )

                    val builder = Request.Builder()
                    listHeaders.forEach {
                        builder.addHeader(
                            readingToStringParser.convert(
                                message,
                                it.key
                            ), readingToStringParser.convert(
                                message,
                                it.value
                            )
                        )
                    }

                    val request = builder
                        .url(restPublish.url)
                        .put(body)
                        .build()
                    httpClient.newCall(request).execute()
                }
                else -> {
                    throw IllegalArgumentException("Illegal Http method please check from getRequestObservable list.")
                }
            }
        }
    }

    override fun getPublishData(): BasePublish {
        return restPublish
    }

    override fun isPublishable(device: Device): Boolean {
        return System.currentTimeMillis() > (restPublish.lastTimeMillis + restPublish.timeMillis)
                && listDevices.contains(device)
    }

    override fun closeConnection() {
        //Do-nothing
    }
}