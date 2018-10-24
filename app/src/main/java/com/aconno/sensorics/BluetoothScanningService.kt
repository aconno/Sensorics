package com.aconno.sensorics

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import com.aconno.sensorics.data.publisher.GoogleCloudPublisher
import com.aconno.sensorics.data.publisher.MqttPublisher
import com.aconno.sensorics.data.publisher.RestPublisher
import com.aconno.sensorics.domain.Publisher
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.ifttt.outcome.RunOutcomeUseCase
import com.aconno.sensorics.domain.interactor.LogReadingUseCase
import com.aconno.sensorics.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.sensorics.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.sensorics.domain.interactor.ifttt.UpdatePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.googlepublish.GetAllEnabledGooglePublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.mqttpublish.GetAllEnabledMqttPublishUseCase
import com.aconno.sensorics.domain.interactor.ifttt.restpublish.GetAllEnabledRestPublishUseCase
import com.aconno.sensorics.domain.interactor.mqtt.CloseConnectionUseCase
import com.aconno.sensorics.domain.interactor.mqtt.PublishReadingsUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.scanning.Bluetooth
import dagger.android.DaggerService
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function4
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class BluetoothScanningService : DaggerService() {

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
    lateinit var getAllEnabledRestPublishUseCase: GetAllEnabledRestPublishUseCase

    @Inject
    lateinit var getAllEnabledMqttPublishUseCase: GetAllEnabledMqttPublishUseCase

    @Inject
    lateinit var updatePublishUseCase: UpdatePublishUseCase

    @Inject
    lateinit var getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase

    @Inject
    lateinit var getDevicesThatConnectedWithRestPublishUseCase: GetDevicesThatConnectedWithRestPublishUseCase

    @Inject
    lateinit var getDevicesThatConnectedWithMqttPublishUseCase: GetDevicesThatConnectedWithMqttPublishUseCase

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

    private var closeConnectionUseCase: CloseConnectionUseCase? = null
    private var publishReadingsUseCase: PublishReadingsUseCase? = null
    private var publishers: MutableList<Publisher>? = null

    private val disposables = CompositeDisposable()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        localBroadcastManager.registerReceiver(receiver, filter)

        startForeground(1, notification)

        val filterByDevice =
            intent?.getBooleanExtra(BLUETOOTH_SCANNING_SERVICE_EXTRA, false) ?: false

        if (filterByDevice && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //send values only while scanning with device filter
            initPublishers()

            disposables.add(
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
            )
        } else {
            bluetooth.startScanning()
            running = true
            startRecording()
            startLogging()
            startSyncing()
            handleInputsForActions()
        }
        return START_STICKY
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
        disposables.add(
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
        )
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
        disposables.add(
            readings.subscribe {
                logReadingsUseCase.execute(it)
            }
        )
    }

    private fun initPublishers() {
        disposables.add(
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
        return getAllEnabledRestPublishUseCase.execute()
            .subscribeOn(Schedulers.io())
            .toObservable()
            .flatMapIterable { it }
            .map { it as RestPublish }
            .flatMap {
                Observable.zip(
                    Observable.just(it),
                    getDevicesThatConnectedWithRestPublishUseCase.execute(it.id).toObservable(),
                    getRestHeadersByIdUseCase.execute(it.id).toObservable(),
                    getRestHttpGetParamsByIdUseCase.execute(it.id).toObservable(),
                    Function4<RestPublish, List<Device>, List<RestHeader>, List<RestHttpGetParam>, Publisher> { t1, t2, t3, t4 ->
                        RestPublisher(
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

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
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