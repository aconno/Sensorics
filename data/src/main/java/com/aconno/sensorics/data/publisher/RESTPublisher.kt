package com.aconno.sensorics.data.publisher

import com.aconno.sensorics.data.converter.NewDataStringConverter
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    private val readingToStringParser: NewDataStringConverter

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        readingToStringParser = NewDataStringConverter()
    }

    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
    }

    override fun publish(readings: List<Reading>) {
        Timber.tag("Publisher HTTP")
            .d("${restPublish.name} publishes from ${readings[0].device}")
        getRequestObservable(readings)
            .flatMapIterable { it }
            .map { it }
            .subscribe {
                Timber.d(it.body().toString())
            }

    }

    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {
        val reading = Reading(
            System.currentTimeMillis(),
            Device("TestDevice", "Name", "MA:CA:DD:RE:SS:11"),
            1,
            "Temperature"
        )

        getRequestObservable(listOf(reading))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapIterable {
                it
            }
            .map {
                it
            }
            .subscribe {
                if (it.isSuccessful) {
                    testConnectionCallback.onConnectionSuccess()
                } else {
                    testConnectionCallback.onConnectionFail()
                }
            }
    }

    private fun getRequestObservable(message: List<Reading>): Observable<List<Response>> {

        return Observable.fromCallable {
            val responseList = mutableListOf<Response>()
            when (restPublish.method) {
                "GET" -> {
                    val json = Gson().toJson(listHttpGetParams)
                    val convert = readingToStringParser.convert(message, json)

                    convert.forEach { it ->
                        val httpGetType =
                            object : TypeToken<List<GeneralRESTHttpGetParam>>() {}.type
                        val list = Gson().fromJson<List<GeneralRESTHttpGetParam>>(it, httpGetType)

                        val httpBuilder = HttpUrl.parse(restPublish.url)!!.newBuilder()

                        list.forEach {
                            httpBuilder.addQueryParameter(
                                it.key,
                                it.value
                            )
                        }

                        val builder = Request.Builder()
                        addHeaders(builder, message[0])

                        val request = builder.url(httpBuilder.build()).build()

                        responseList.add(
                            httpClient.newCall(request).execute()
                        )
                    }

                }
                "POST" -> {
                    val convert = readingToStringParser.convert(message, restPublish.dataString)

                    convert.forEach {
                        val body = RequestBody.create(
                            getMediaType(),
                            it
                        )

                        val builder = Request.Builder()
                        addHeaders(builder, message[0])

                        val request = builder
                            .url(restPublish.url)
                            .post(body)
                            .build()

                        responseList.add(
                            httpClient.newCall(request).execute()
                        )
                    }

                }
                "PUT" -> {
                    val convert = readingToStringParser.convert(message, restPublish.dataString)

                    convert.forEach {
                        val body = RequestBody.create(
                            getMediaType(),
                            it
                        )

                        val builder = Request.Builder()
                        addHeaders(builder, message[0])

                        val request = builder
                            .url(restPublish.url)
                            .put(body)
                            .build()

                        responseList.add(
                            httpClient.newCall(request).execute()
                        )
                    }

                }

            }

            responseList
        }

    }

    private fun getMediaType(): MediaType? {
        listHeaders.forEach {
            if (it.key == "Content-Type") {
                return MediaType.parse(it.value)
            }
        }

        return JSON
    }

    private fun addHeaders(
        builder: Request.Builder,
        message: Reading
    ) {
        listHeaders.forEach {
            builder.addHeader(
                readingToStringParser.convertDeviceAndTS(message, it.key),
                readingToStringParser.convertDeviceAndTS(message, it.value)
            )
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