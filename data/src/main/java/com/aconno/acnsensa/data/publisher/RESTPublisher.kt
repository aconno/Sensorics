package com.aconno.acnsensa.data.publisher

import com.aconno.acnsensa.data.converter.PublisherDataConverter
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.RESTHeader
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.model.ReadingType
import com.aconno.acnsensa.domain.model.Device
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

class RESTPublisher(
    private val restPublish: RESTPublish,
    private val listDevices: List<Device>,
    private val listHeaders: List<RESTHeader>
) : Publisher {

    private val httpClient: OkHttpClient

    init {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.HEADERS
        httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    companion object {
        private val JSON = MediaType.parse("application/json; charset=utf-8")
    }

    override fun publish(reading: Reading) {
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
            Reading(
                System.currentTimeMillis(),
                Device("TestDevice", "MA:CA:DD:RE:SS:11"),
                1,
                ReadingType.OTHER
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
                    httpBuilder.addQueryParameter(restPublish.parameterName, message)

                    val builder = Request.Builder()
                    listHeaders.forEach {
                        builder.addHeader(it.key, it.value)
                    }

                    val request = builder.url(httpBuilder.build()).build();

                    httpClient.newCall(request).execute()
                }
                restPublish.method == "POST" -> {
                    val body = RequestBody.create(JSON, message)

                    val builder = Request.Builder()
                    listHeaders.forEach {
                        builder.addHeader(it.key, it.value)
                    }

                    val request = builder
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