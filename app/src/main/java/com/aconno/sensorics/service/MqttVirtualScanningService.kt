package com.aconno.sensorics.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.aconno.sensorics.R
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.interactor.consolidation.GenerateReadingsUseCase
import com.aconno.sensorics.domain.interactor.virtualscanningsource.mqtt.GetAllEnabledMqttVirtualScanningSourceUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.mqtt.MqttVirtualScanner
import com.aconno.sensorics.domain.virtualscanningsources.mqtt.MqttVirtualScanningSource
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class MqttVirtualScanningService : ScanningService() {

    @Inject
    lateinit var generateReadingsUseCase: GenerateReadingsUseCase

    @Inject
    lateinit var getAllEnabledMqttVirtualScanningSourceUseCase: GetAllEnabledMqttVirtualScanningSourceUseCase

    @Inject
    @Named("mqttReadings")
    override lateinit var readings: Flowable<List<Reading>>

    @Inject
    lateinit var mqttVirtualScanner: MqttVirtualScanner

    private var publishers: MutableList<Publisher<*>>? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        mqttVirtualScanner.scanningConnectionCallback = object : MqttVirtualScanner.ConnectionCallback {
            override fun onConnectionFail(source: MqttVirtualScanningSource, exception: Throwable?) {
                Toast.makeText(this@MqttVirtualScanningService,
                        this@MqttVirtualScanningService.getString(R.string.virtual_scanning_source_connection_fail, source.name),
                        Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onConnectionSuccess(source: MqttVirtualScanningSource) {}
        }

        Single.fromCallable {
                getAllEnabledMqttVirtualScanningSourceUseCase.execute()
            }
            .subscribeOn(Schedulers.io())
            .subscribe { sources ->
                if (sources.isNotEmpty()) {
                    sources.forEach { source ->
                        val mqttSource = source as MqttVirtualScanningSource
                        mqttVirtualScanner.addSource(
                            mqttSource
                        )
                    }

                    getSavedDevicesUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { devices ->
                            mqttVirtualScanner.addDevicesToScanFor(devices)
                        }.also {
                            disposables.add(it)
                        }

                    startScan()

                }
            }.also { disposables.add(it) }

        return START_STICKY
    }


    private fun startScan() {
        TAG.d("Started")

        mqttVirtualScanner.startScanning()

        running = true
        startRecording()
        startLogging()
        handleInputsForActions()
    }

    fun stopScanning() {
        stopRecording()
        mqttVirtualScanner.stopScanning()
        mqttVirtualScanner.clearSources()
        running = false
        stopSelf()
        publishers = null
    }

    companion object {
        const val STOP: String = "com.aconno.sensorics.mqttvss.STOP"

        fun start(context: Context) {
            val intent = Intent(context, MqttVirtualScanningService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private val TAG: Timber.Tree = Timber.tag("MQTT VSS")

        private var running = false

        fun isRunning(): Boolean { // TODO: Add next to other scanning services
            return running
        }
    }
}