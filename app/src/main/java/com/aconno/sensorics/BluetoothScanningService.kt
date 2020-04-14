package com.aconno.sensorics

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.data.publisher.AzureMqttPublisher
import com.aconno.sensorics.data.publisher.GoogleCloudPublisher
import com.aconno.sensorics.data.publisher.MqttPublisher
import com.aconno.sensorics.data.publisher.RestPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.AzureMqttPublish
import com.aconno.sensorics.domain.ifttt.GooglePublish
import com.aconno.sensorics.domain.ifttt.MqttPublish
import com.aconno.sensorics.domain.ifttt.RestPublish
import com.aconno.sensorics.domain.ifttt.outcome.RunOutcomeUseCase
import com.aconno.sensorics.domain.interactor.LogReadingUseCase
import com.aconno.sensorics.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.sensorics.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.sensorics.domain.interactor.ifttt.publish.GetAllEnabledPublishersUseCase
import com.aconno.sensorics.domain.interactor.mqtt.CloseConnectionUseCase
import com.aconno.sensorics.domain.interactor.mqtt.PublishReadingsUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.repository.SyncRepository
import com.aconno.sensorics.domain.scanning.Bluetooth
import dagger.android.DaggerService
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named


class BluetoothScanningService : DaggerService() {

    @Inject
    lateinit var bluetooth: Bluetooth

    @Inject
    @Named("bluetoothReadings")
    lateinit var readings: Flowable<List<Reading>>

    @Inject
    lateinit var saveSensorReadingsUseCase: SaveSensorReadingsUseCase

    @Inject
    lateinit var logReadingsUseCase: LogReadingUseCase

    @Inject
    lateinit var inputToOutcomesUseCase: InputToOutcomesUseCase

    @Inject
    lateinit var getAllEnabledPublishersUseCase: GetAllEnabledPublishersUseCase

    @Inject
    lateinit var getDevicesConnectedWithPublishUseCase: GetDevicesConnectedWithPublishUseCase

    @Inject
    lateinit var getRestHeadersByIdUseCase: GetRestHeadersByIdUseCase

    @Inject
    lateinit var getRestHttpGetParamsByIdUseCase: GetRestHttpGetParamsByIdUseCase

    @Inject
    lateinit var runOutcomeUseCase: RunOutcomeUseCase

    @Inject
    lateinit var receiver: BroadcastReceiver

    @Inject
    lateinit var filter: IntentFilter

    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    @Inject
    lateinit var readingToInputUseCase: ReadingToInputUseCase

    @Inject
    lateinit var getSavedDevicesMaybeUseCase: GetSavedDevicesMaybeUseCase

    @Inject
    lateinit var syncRepository: SyncRepository

    private var closeConnectionUseCase: CloseConnectionUseCase? = null
    private var publishReadingsUseCase: PublishReadingsUseCase? = null
    private var publishers: MutableList<Publisher<*>>? = null

    private lateinit var scanTimerDisposable: Job

    private val disposables = CompositeDisposable()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        localBroadcastManager.registerReceiver(receiver, filter)

        startForeground(1, notification)

        val filterByDevice =
            intent?.getBooleanExtra(BLUETOOTH_SCANNING_SERVICE_EXTRA, false) ?: false

        if (filterByDevice) {
            //send values only while scanning with device filter
            initPublishers()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                disposables.add(
                    getSavedDevicesMaybeUseCase.execute()
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

                    publishReadingsUseCase?.execute(readings)
                }
            )
        }
    }

    private fun handleInputsForActions() {
        Timber.i("handle Action..... $readings")

        disposables.add(
            readings
                .concatMap {
                    readingToInputUseCase.execute(it).toFlowable()
                }
                .flatMapIterable { it }
                .concatMap {
                    inputToOutcomesUseCase.execute(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .toFlowable()
                }
                .flatMapIterable { it }
                .subscribe {
                    runOutcomeUseCase.execute(it)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                }

        )
    }

    fun stopScanning() {
        scanTimerDisposable.cancel()
        stopRecording()
        closeConnectionUseCase?.execute()
        bluetooth.stopScanning()
        running = false
        stopSelf()
        publishReadingsUseCase = null
        closeConnectionUseCase = null
        publishers = null
    }

    private var recordReadingsDisposable: Disposable? = null

    private fun startRecording() {
        recordReadingsDisposable = readings.subscribe {
            saveSensorReadingsUseCase.execute(it)
        }
    }

    private fun stopRecording() {
        recordReadingsDisposable?.dispose()
    }

    private fun startLogging() {
        disposables.add(
            readings.subscribe {
                logReadingsUseCase.execute(it)
            }
        )
    }

    private fun initPublishers() {
        GlobalScope.launch(Dispatchers.Default) {
            disposables.add(
                getAllEnabledPublishersObservable()
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        Consumer {
                            publishers = if (it.size < 1) {
                                null
                            } else {
                                it
                            }

                            if (publishers != null) {
                                publishReadingsUseCase = PublishReadingsUseCase(publishers!!)
                                closeConnectionUseCase = CloseConnectionUseCase(publishers!!)
                            }
                        }
                    )
            )

        }
    }

    private fun getAllEnabledPublishersObservable(): Observable<Publisher<*>> {
        return getAllEnabledPublishersUseCase.execute()
            .subscribeOn(Schedulers.io())
            .toObservable()
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

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
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