package com.aconno.acnsensa

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.dagger.bluetoothscanning.BluetoothScanningServiceComponent
import com.aconno.acnsensa.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.acnsensa.dagger.bluetoothscanning.DaggerBluetoothScanningServiceComponent
import com.aconno.acnsensa.data.publisher.GoogleCloudPublisher
import com.aconno.acnsensa.data.publisher.MqttPublisher
import com.aconno.acnsensa.data.publisher.RESTPublisher
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.MqttPublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.ifttt.outcome.RunOutcomeUseCase
import com.aconno.acnsensa.domain.interactor.LogReadingUseCase
import com.aconno.acnsensa.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.acnsensa.domain.model.Reading
import com.aconno.acnsensa.domain.interactor.ifttt.*
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.GetAllEnabledGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.gpublish.UpdateGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.GetAllEnabledMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.mpublish.UpdateMqttPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.GetAllEnabledRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.rpublish.UpdateRESTPublishUserCase
import com.aconno.acnsensa.domain.interactor.mqtt.CloseConnectionUseCase
import com.aconno.acnsensa.domain.interactor.mqtt.PublishReadingsUseCase
import com.aconno.acnsensa.domain.interactor.repository.*
import com.aconno.acnsensa.domain.scanning.Bluetooth
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
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

    private var closeConnectionUseCase: CloseConnectionUseCase? = null
    private var publishReadingsUseCase: PublishReadingsUseCase? = null
    private var publishers: MutableList<Publisher>? = null

    private val bluetoothScanningServiceComponent: BluetoothScanningServiceComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerBluetoothScanningServiceComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
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

        initPublishers()

        bluetooth.startScanning()
        running = true
        startRecording()
        startLogging()
        startSyncing()
        handleInputsForActions()
        return START_STICKY
    }

    private fun startSyncing() {
        readings.subscribe {
            //Publish when Google Cloud Integration Enabled
            publishReadingsUseCase?.execute(it)
                ?.subscribeOn(Schedulers.io())
                ?.flatMapIterable { it }
                ?.map {
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
                ?.subscribe {
                    it.subscribeOn(Schedulers.io()).subscribe()
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
        closeConnectionUseCase?.execute()
        bluetooth.stopScanning()
        running = false
        stopSelf()
        publishReadingsUseCase = null
        closeConnectionUseCase = null
        publishers = null
    }

    private fun startRecording() {
        readings.subscribe {
            saveSensorReadingsUseCase.execute(it)
        }
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
                Observable.just(it).zipWith(
                    getDevicesThatConnectedWithRESTPublishUseCase.execute(it.id)
                        .toObservable().zipWith(getRESTHeadersByIdUseCase.execute(it.id).toObservable())
                )
            }.map {
                RESTPublisher(
                    it.first,
                    it.second.first,
                    it.second.second
                ) as Publisher
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

        fun start(context: Context) {
            val intent = Intent(context, BluetoothScanningService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        private var running = false

        fun isRunning(): Boolean {
            return running
        }
    }
}