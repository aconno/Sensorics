package com.aconno.acnsensa.data.http

import com.aconno.acnsensa.data.mqtt.GoogleCloudDataConverter
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.model.SensorType
import com.aconno.acnsensa.domain.model.readings.Reading
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import timber.log.Timber
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class RESTPublisher(private val restPublish: RESTPublish) : Publisher {

    private val httpClient: OkHttpClient
    private var testConnectionCallback: Publisher.TestConnectionCallback? = null

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BASIC
        httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
        private const val GET_PARAMETERNAME = "ACNSensa"
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

    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {
        this.testConnectionCallback = testConnectionCallback

        val convertList = GoogleCloudDataConverter.convert(
            Reading(
                listOf(10, 15, 20),
                System.currentTimeMillis(),
                SensorType.LIGHT
            )
        )

        getRequestObservable(convertList[0])
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                if (it.isSuccessful) {
                    this.testConnectionCallback?.onSuccess()
                } else {
                    this.testConnectionCallback?.onFail()
                }
            }
    }

    private fun getRequestObservable(message: String): Observable<Response> {
        return Observable.fromCallable {
            when {
                restPublish.method == "GET" -> {
                    val httpBuilder = HttpUrl.parse(restPublish.url)!!.newBuilder()
                    httpBuilder.addQueryParameter(GET_PARAMETERNAME, message)

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

    override fun isPublishable(): Boolean {
        return System.currentTimeMillis() > (restPublish.lastTimeMillis + restPublish.timeMillis)
    }

    override fun closeConnection() {
        //Do-nothing
    }


}