package com.aconno.sensorics.service

import android.content.Context
import android.content.Intent
import android.os.Build
import com.aconno.sensorics.data.publisher.AzureMqttPublisher
import com.aconno.sensorics.data.publisher.GoogleCloudPublisher
import com.aconno.sensorics.data.publisher.MqttPublisher
import com.aconno.sensorics.data.publisher.RestPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllEnabledPublishersUseCase
import com.aconno.sensorics.domain.interactor.mqtt.CloseConnectionUseCase
import com.aconno.sensorics.domain.interactor.mqtt.PublishReadingsUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.scanning.Bluetooth
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class BluetoothScanningService : ScanningService() {

    @Inject
    lateinit var bluetooth: Bluetooth

    @Inject
    lateinit var publishReadingsUseCase: PublishReadingsUseCase

    @Inject
    lateinit var closeConnectionUseCase: CloseConnectionUseCase

    @Inject
    lateinit var getAllEnabledPublishersUseCase: GetAllEnabledPublishersUseCase

    @Inject
    @Named("bluetoothReadings")
    override lateinit var readings: Flowable<List<Reading>>

    private var publishers: MutableList<Publisher<*>> = mutableListOf()

    private lateinit var scanTimerDisposable: Job

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val filterByDevice =
            intent?.getBooleanExtra(BLUETOOTH_SCANNING_SERVICE_EXTRA, false) ?: false

        if (filterByDevice) {
            //send values only while scanning with device filter
            initPublishers()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                disposables.add(
                    getSavedDevicesUseCase.execute()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            startScan(it)
                        }
                )
            } else {
                //If Lower Than M Ignore Filtering By Mac Address
                startScan()
            }

        } else {
            startScan()
        }

        return START_STICKY
    }


    /**ss
     * Restart scanning before Android BLE Scanning Timeout
     * https://github.com/aconno/Sensorics/issues/12
     */
    private fun startTimer(deviceList: List<Device>? = null) {
        //Launches non-blocking coroutine
        scanTimerDisposable = GlobalScope.launch(context = Dispatchers.Main) {
            delay(ANDROID_N_MAX_SCAN_DURATION - 60 * 1000)
            Timber.tag("Sensorics - BLE").d("Stopping")
            stopScanning()
            Timber.tag("Sensorics - BLE").d("Restarting")
            startScan(deviceList)
        }
    }

    private fun startScan(deviceList: List<Device>? = null) {
        startTimer(deviceList)
        Timber.tag("Sensorics - BLE").d("Started")

        if (deviceList == null) {
            bluetooth.startScanning()

        } else {
            bluetooth.startScanning(deviceList)
        }
        running = true
        startRecording()
        startLogging()
        startSyncing()
        handleInputsForActions()
    }

    private fun startSyncing() {
        GlobalScope.launch(Dispatchers.Default) {
            disposables.add(
                readings.subscribe { readings ->
                    //Send data and update last sent date-time

                    publishReadingsUseCase.execute(publishers, readings)
                }
            )
        }
    }

    fun stopScanning() {
        scanTimerDisposable.cancel()
        stopRecording()
        closeConnectionUseCase.execute(publishers)
        bluetooth.stopScanning()
        running = false
        stopSelf()
    }

    private fun initPublishers() {
        GlobalScope.launch(Dispatchers.Default) {
            disposables.add(
                getAllEnabledPublishersObservable()
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        Consumer {
                            publishers = it
                        }
                    )
            )
        }
    }

    private fun getAllEnabledPublishersObservable(): Observable<Publisher<*>> {
        return getAllEnabledPublishersUseCase.execute()
            .subscribeOn(Schedulers.io())
            .toObservable()
            .flatMapIterable { it }
            .map {
                when (it) {
                    is GooglePublish -> {
                        GoogleCloudPublisher(
                            this,
                            it,
                            getDevicesConnectedWithPublishUseCase.execute(
                                it.id, it.type
                            ).blockingGet(),
                            syncRepository
                        )
                    }
                    is RestPublish -> {
                        RestPublisher(
                            it,
                            getDevicesConnectedWithPublishUseCase.execute(
                                it.id, it.type
                            ).blockingGet(),
                            getRestHeadersByIdUseCase.execute(
                                it.id
                            ).blockingGet(),
                            getRestHttpGetParamsByIdUseCase.execute(
                                it.id
                            ).blockingGet(),
                            syncRepository
                        )
                    }
                    is MqttPublish -> {
                        MqttPublisher(
                            this,
                            it,
                            getDevicesConnectedWithPublishUseCase.execute(
                                it.id, it.type
                            ).blockingGet(),
                            syncRepository
                        )
                    }
                    is AzureMqttPublish -> {
                        AzureMqttPublisher(
                            it,
                            getDevicesConnectedWithPublishUseCase.execute(
                                it.id, it.type
                            ).blockingGet(),
                            syncRepository
                        )
                    }
                    else -> throw IllegalStateException("A publisher was not implemented")
                }
            }
    }

    companion object {
        private const val ANDROID_N_MAX_SCAN_DURATION = 30 * 60 * 1000L // 30 minutes

        fun start(context: Context, filterByDevice: Boolean = true) {
            val intent = Intent(context, BluetoothScanningService::class.java)
            intent.putExtra(BLUETOOTH_SCANNING_SERVICE_EXTRA, filterByDevice)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private const val BLUETOOTH_SCANNING_SERVICE_EXTRA = "BLUETOOTH_SCANNING_SERVICE_EXTRA"

        private var running = false

        fun isRunning(): Boolean {
            return running
        }
    }
}