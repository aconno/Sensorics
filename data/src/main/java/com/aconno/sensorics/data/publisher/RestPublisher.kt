package com.aconno.sensorics.data.publisher

import android.annotation.SuppressLint
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.GeneralRestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestHeader
import com.aconno.sensorics.domain.ifttt.RestHttpGetParam
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.Sync
import com.aconno.sensorics.domain.repository.SyncRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RestPublisher(
    publish: RestPublish,
    listDevices: List<Device>,
    private val listHeaders: List<RestHeader>,
    private val listHttpGetParams: List<RestHttpGetParam>,
    syncRepository: SyncRepository
) : Publisher<RestPublish>(
    publish, listDevices, syncRepository
) {
    private val httpClient: OkHttpClient
    private val readingToStringParser: DataStringConverter

    init {

        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }
        })

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val socketFactory = sslContext.socketFactory


        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient = OkHttpClient.Builder()
            .sslSocketFactory(socketFactory, trustAllCerts[0] as X509TrustManager)
            .addInterceptor(logging)
            .build()

        readingToStringParser = DataStringConverter()
    }

    companion object {
        private val JSON = "application/json".toMediaTypeOrNull()
    }

    @SuppressLint("CheckResult")
    override fun publish(readings: List<Reading>) {
        if (readings.isNotEmpty() && isPublishable(readings)) {
            Timber.tag("Publisher HTTP")
                .d("${publish.name} publishes from ${readings[0].device}")
            getRequestObservable(readings)
                .flatMapIterable { it }
                .map { it }
                .subscribe(
                    {
                        Timber.d(it.body.toString())
                    }, {
                    //No-Op
                }
                )

            val reading = readings.first()
            val time = System.currentTimeMillis()
            Timber.tag("ConvertedString").d("Syncing")
            syncRepository.save(
                Sync(
                    "rest" + publish.id,
                    reading.device.macAddress,
                    reading.advertisementId,
                    time
                )
            )
            lastSyncs[Pair(reading.device.macAddress, reading.advertisementId)] = time
        }

    }

    @SuppressLint("CheckResult")
    override fun test(testConnectionCallback: Publisher.TestConnectionCallback) {
        val reading = Reading(
            System.currentTimeMillis(),
            Device("TestDevice", "Name", "MA:CA:DD:RE:SS:11"),
            1,
            "Temperature",
            0,
            "AdvertisementId",
            null
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
            .subscribe(
                {
                    if (it.isSuccessful) {
                        testConnectionCallback.onConnectionSuccess()
                    } else {
                        testConnectionCallback.onConnectionFail(null)
                    }
                },
                {
                    testConnectionCallback.onConnectionFail(it)
                }
            )
    }

    private fun getRequestObservable(message: List<Reading>): Observable<List<Response>> {
        Timber.tag("ConvertedString").d("getRequestObservable")
        return Observable.fromCallable {
            val responseList = mutableListOf<Response>()
            when (publish.method) {
                "GET" -> {
                    val json = Gson().toJson(listHttpGetParams)
                    val convert = readingToStringParser.convert(message, json)

                    convert.forEach { it ->
                        val httpGetType =
                            object : TypeToken<List<GeneralRestHttpGetParam>>() {}.type
                        val list = Gson().fromJson<List<GeneralRestHttpGetParam>>(it, httpGetType)

                        val httpBuilder = publish.url.toHttpUrlOrNull()!!.newBuilder()

                        list.forEach {
                            httpBuilder.addQueryParameter(
                                it.key,
                                it.value
                            )
                        }

                        val builder = Request.Builder()
                        addHeaders(builder, message[0])

                        val request = builder.url(httpBuilder.build()).build()

                        var response: Response? = null
                        var tries = 3
                        while (tries-- > 0) {
                            try {
                                response = httpClient.newCall(request).execute()
                                break
                            } catch (e: IOException) {
                                Timber.d(e)
                            }
                        }
                        response?.let { responseList.add(it) }
                    }

                }
                "POST" -> {
                    val i = Random().nextInt(16)

                    val convert = readingToStringParser.convert(message, publish.dataString)

                    convert.forEach {
                        val body = RequestBody.create(
                            getMediaType(),
                            it.toByteArray()
                        )

                        val builder = Request.Builder()
                        addHeaders(builder, message[0])

                        val request = builder
                            .url(publish.url)
                            .post(body)
                            .build()

                        var response: Response? = null
                        var tries = 3
                        while (tries-- > 0) {
                            try {
                                response = httpClient.newCall(request).execute()
                                break
                            } catch (e: IOException) {
                                Timber.d(e)
                            }
                        }
                        response?.let { responseList.add(it) }
                    }
                }
                "PUT" -> {
                    val convert = readingToStringParser.convert(message, publish.dataString)

                    convert.forEach {
                        val body = RequestBody.create(
                            getMediaType(),
                            it
                        )

                        val builder = Request.Builder()
                        addHeaders(builder, message[0])

                        val request = builder
                            .url(publish.url)
                            .put(body)
                            .build()

                        var response: Response? = null
                        var tries = 3
                        while (tries-- > 0) {
                            try {
                                response = httpClient.newCall(request).execute()
                                break
                            } catch (e: IOException) {
                                Timber.d(e)
                            }
                        }
                        response?.let { responseList.add(it) }
                    }

                }

            }
            responseList
        }
    }

    private fun getMediaType(): MediaType? {
        listHeaders.forEach {
            if (it.key == "Content-Type") {
                return it.value.toMediaTypeOrNull()
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

    override fun closeConnection() {
        //Do-nothing
    }
}