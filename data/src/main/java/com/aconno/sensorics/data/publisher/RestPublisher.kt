package com.aconno.sensorics.data.publisher

import android.annotation.SuppressLint
import com.aconno.sensorics.data.converter.DataStringConverter
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.*
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
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RestPublisher(
    private val restPublish: RestPublish,
    private val listDevices: List<Device>,
    private val listHeaders: List<RestHeader>,
    private val listHttpGetParams: List<RestHttpGetParam>,
    private val syncRepository: SyncRepository
) : Publisher {
    private val lastSyncs: MutableMap<Pair<String, String>, Long> =
        syncRepository.getSync("rest" + restPublish.id)
            .map { Pair(it.macAddress, it.advertisementId) to it.lastSyncTimestamp }
            .toMap()
            .toMutableMap()

    private val httpClient: OkHttpClient
    private val readingToStringParser: DataStringConverter

    init {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        httpClient = OkHttpClient.Builder()
            .socketFactory(getUnsafeSSLSocketFactory())
            .addInterceptor(logging)
            .build()

        readingToStringParser = DataStringConverter()
    }

    private fun getUnsafeSSLSocketFactory(): SSLSocketFactory {
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
        val sslSocketFactory = sslContext.socketFactory
        return sslSocketFactory
    }

    companion object {
        private val JSON = MediaType.parse("application/json")
    }

    @SuppressLint("CheckResult")
    override fun publish(readings: List<Reading>) {
        if (readings.isNotEmpty() && isPublishable(readings)) {
            Timber.tag("Publisher HTTP")
                .d("${restPublish.name} publishes from ${readings[0].device}")
            getRequestObservable(readings)
                .flatMapIterable { it }
                .map { it }
                .subscribe(
                    {
                        Timber.d(it.body().toString())
                    }, {
                        //No-Op
                    }
                )

            val reading = readings.first()
            val time = System.currentTimeMillis()
            syncRepository.save(
                Sync(
                    "rest" + restPublish.id,
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
            "AdvertisementId"
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
                            object : TypeToken<List<GeneralRestHttpGetParam>>() {}.type
                        val list = Gson().fromJson<List<GeneralRestHttpGetParam>>(it, httpGetType)

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
                            it.toByteArray()
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

    private fun isPublishable(readings: List<Reading>): Boolean {
        val reading = readings.firstOrNull()
        val latestTimestamp =
            lastSyncs[Pair(reading?.device?.macAddress, reading?.advertisementId)] ?: 0

        return System.currentTimeMillis() - latestTimestamp > this.restPublish.timeMillis
                && reading != null && listDevices.contains(reading.device)
    }

    override fun closeConnection() {
        //Do-nothing
    }
}