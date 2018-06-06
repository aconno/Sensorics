package com.aconno.acnsensa.data.publisher

import com.aconno.acnsensa.data.converter.PublisherDataConverter
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.model.SensorTypeSingle
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber


class RESTPublisher(
    private val restPublish: RESTPublish,
    private val listDevices: List<Device>
) : Publisher {

    private val httpClient: OkHttpClient

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        private const val GET_PARAMETER_NAME = "ACNSensa"
    }

    override fun publish(reading: SensorReading) {
        val messages = PublisherDataConverter.convert(reading)

        Observable.fromIterable(messages)
            .subscribeOn(Schedulers.io())
            .concatMap { it ->
                Timber.tag("Publisher HTTP")
                    .d("${restPublish.name} publishes from ${reading.device}")
                getRequestObservable(it)
            }
            .subscribe {
                Timber.d(it.body().toString())
            }
    }

    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {

        val convertList = PublisherDataConverter.convert(
            SensorReading(
                System.currentTimeMillis(),
                Device("TestDevice", "MA:CA:DD:RE:SS:11"),
                20,
                SensorTypeSingle.LIGHT
            )
        )

        getRequestObservable(convertList[0])
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isSuccessful) {
                    testConnectionCallback.onSuccess()
                } else {
                    testConnectionCallback.onFail()
                }
            }
    }

    private fun getRequestObservable(message: String): Observable<Response> {
        return Observable.fromCallable {
            when {
                restPublish.method == "GET" -> {
                    val httpBuilder = HttpUrl.parse(restPublish.url)!!.newBuilder()
                    httpBuilder.addQueryParameter(GET_PARAMETER_NAME, message)

                    httpClient.newCall(Request.Builder().url(httpBuilder.build()).build()).execute()
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