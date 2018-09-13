package com.aconno.sensorics

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceComponent
import com.aconno.sensorics.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.sensorics.dagger.bluetoothscanning.DaggerBluetoothScanningServiceComponent
import com.aconno.sensorics.data.publisher.GoogleCloudPublisher
import com.aconno.sensorics.data.publisher.MqttPublisher
import com.aconno.sensorics.data.publisher.RESTPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.ifttt.outcome.RunOutcomeUseCase
import com.aconno.sensorics.domain.interactor.LogReadingUseCase
import com.aconno.sensorics.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.sensorics.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.GetAllEnabledGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.gpublish.UpdateGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.GetAllEnabledMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mpublish.UpdateMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.GetAllEnabledRESTPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.rpublish.UpdateRESTPublishUserCase
import com.aconno.sensorics.domain.interactor.mqtt.CloseConnectionUseCase
import com.aconno.sensorics.domain.interactor.mqtt.PublishReadingsUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.scanning.Bluetooth
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function4
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject


class BluetoothScanningService : Service() {

    @Inject
    lateinit var bluetooth: Bluetooth

    @Inject
    lateinit var readings: Flowable<List<Reading>>

    @Inject
    lateinit var saveSensorReadingsUseCase: SaveSensorReadingsUseCase

    @Inject
    lateinit var logReadingsUseCase: LogReadingUseCase

    @Inject
    lateinit var inputToOutcomesUseCase: InputToOutcomesUseCase

    @Inject
    lateinit var getAllEnabledGooglePublishUseCase: GetAllEnabledGooglePublishUseCase

    @Inject
    lateinit var getAllEnabledRESTPublishUseCase: GetAllEnabledRESTPublishUseCase

    @Inject
    lateinit var getAllEnabledMqttPublishUseCase: GetAllEnabledMqttPublishUseCase

    @Inject
    lateinit var updateRESTPublishUserCase: UpdateRESTPublishUserCase

    @Inject
    lateinit var updateGooglePublishUseCase: UpdateGooglePublishUseCase

    @Inject
    lateinit var updateMqttPublishUseCase: UpdateMqttPublishUseCase

    @Inject
    lateinit var getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase

    @Inject
    lateinit var getDevicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase

    @Inject
    lateinit var getDevicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase

    @Inject
    lateinit var getRESTHeadersByIdUseCase: GetRESTHeadersByIdUseCase

    @Inject
    lateinit var getRESTHttpGetParamsByIdUseCase: GetRESTHttpGetParamsByIdUseCase

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

    private var closeConnectionUseCase: CloseConnectionUseCase? = null
    private var publishReadingsUseCase: PublishReadingsUseCase? = null
    private var publishers: MutableList<Publisher>? = null

    private val bluetoothScanningServiceComponent: BluetoothScanningServiceComponent by lazy {
        val sensoricsApplication: SensoricsApplication? = application as? SensoricsApplication
        DaggerBluetoothScanningServiceComponent.builder()
            .appComponent(sensoricsApplication?.appComponent)
            .bluetoothScanningServiceModule(BluetoothScanningServiceModule(this))
            .build()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        bluetoothScanningServiceComponent.inject(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        localBroadcastManager.registerReceiver(receiver, filter)

        startForeground(1, notification)

        val filterByDevice = intent!!.getBooleanExtra(BLUETOOTH_SCANNING_SERVICE_EXTRA, true)

        if (filterByDevice) {
            //send values only while scanning with device filter
            initPublishers()

            getSavedDevicesMaybeUseCase.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    bluetooth.startScanning(it)
                    running = true
                    startRecording()
                    startLogging()
                    startSyncing()
                    handleInputsForActions()
                }
        } else {
            bluetooth.startScanning()
            running = true
            startRecording()
            startLogging()
            startSyncing()
            handleInputsForActions()
        }
        return START_REDELIVER_INTENT
    }

    private fun startSyncing() {
        readings.subscribe { readings ->
            //Send data and update last sent date-time

            publishReadingsUseCase?.let {
                it.execute(readings)
                    .subscribeOn(Schedulers.io())
                    .flatMapIterable { it }
                    .map {
                        val data = it.getPublishData()
                        data.lastTimeMillis = System.currentTimeMillis()

                        when (data) {
                            is GooglePublish -> {
                                updateGooglePublishUseCase.execute(data)
                            }
                            is RESTPublish -> {
                                updateRESTPublishUserCase.execute(data)
                            }
                            is MqttPublish -> {
                                updateMqttPublishUseCase.execute(data)
                            }
                            else -> {
                                throw IllegalArgumentException("Illegal data provided.")
                            }
                        }
                    }
                    .subscribe {
                        it.subscribeOn(Schedulers.io()).subscribe()
                    }
            }
        }
    }

    private fun handleInputsForActions() {
        readings
            .concatMap { readingToInputUseCase.execute(it).toFlowable() }
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
    }

    fun stopScanning() {
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
        readings.subscribe {
            logReadingsUseCase.execute(it)
        }
    }

    private fun initPublishers() {
        Observable.merge(
            getGooglePublisherObservable(),
            getRestPublisherObservable(),
            getMqttPublisherObservable()
        )
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

    }

    private fun getGooglePublisherObservable(): Observable<Publisher> {
        return getAllEnabledGooglePublishUseCase.execute()
            .subscribeOn(Schedulers.io())
            .toObservable()
            .flatMapIterable { it }
            .map { it as GooglePublish }
            .flatMap {
                Observable.just(it).zipWith(
                    getDevicesThatConnectedWithGooglePublishUseCase.execute(it.id)
                        .toObservable()
                )
            }.map {
                GoogleCloudPublisher(
                    this,
                    it.first,
                    it.second
                ) as Publisher
            }
    }

    private fun getRestPublisherObservable(): Observable<Publisher> {
        return getAllEnabledRESTPublishUseCase.execute()
            .subscribeOn(Schedulers.io())
            .toObservable()
            .flatMapIterable { it }
            .map { it as RESTPublish }
            .flatMap {
                Observable.zip(
                    Observable.just(it),
                    getDevicesThatConnectedWithRESTPublishUseCase.execute(it.id).toObservable(),
                    getRESTHeadersByIdUseCase.execute(it.id).toObservable(),
                    getRESTHttpGetParamsByIdUseCase.execute(it.id).toObservable(),
                    Function4<RESTPublish, List<Device>, List<RESTHeader>, List<RESTHttpGetParam>, Publisher> { t1, t2, t3, t4 ->
                        RESTPublisher(
                            t1,
                            t2,
                            t3,
                            t4
                        )
                    }
                )
            }
    }

    private fun getMqttPublisherObservable(): Observable<Publisher> {
        return getAllEnabledMqttPublishUseCase.execute()
            .subscribeOn(Schedulers.io())
            .toObservable()
            .flatMapIterable { it }
            .map { it as MqttPublish }
            .flatMap {
                Observable.just(it).zipWith(
                    getDevicesThatConnectedWithMqttPublishUseCase.execute(it.id)
                        .toObservable()
                )
            }.map {
                MqttPublisher(
                    this,
                    it.first,
                    it.second
                ) as Publisher
            }
    }

    companion object {

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