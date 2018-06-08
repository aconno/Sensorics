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
import com.aconno.acnsensa.data.publisher.EmptyPublisher
import com.aconno.acnsensa.data.publisher.GoogleCloudPublisher
import com.aconno.acnsensa.data.publisher.RESTPublisher
import com.aconno.acnsensa.domain.Publisher
import com.aconno.acnsensa.domain.ifttt.BasePublish
import com.aconno.acnsensa.domain.ifttt.GooglePublish
import com.aconno.acnsensa.domain.ifttt.RESTPublish
import com.aconno.acnsensa.domain.ifttt.outcome.RunOutcomeUseCase
import com.aconno.acnsensa.domain.interactor.LogReadingUseCase
import com.aconno.acnsensa.domain.interactor.convert.SensorReadingToInputUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.*
import com.aconno.acnsensa.domain.interactor.mqtt.CloseConnectionUseCase
import com.aconno.acnsensa.domain.interactor.mqtt.PublishReadingsUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithGooglePublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetDevicesThatConnectedWithRESTPublishUseCase
import com.aconno.acnsensa.domain.interactor.repository.SaveSensorReadingsUseCase
import com.aconno.acnsensa.domain.interactor.repository.SensorValuesToReadingsUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.scanning.Bluetooth
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.zipWith
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author aconno
 */
class BluetoothScanningService : Service() {

    @Inject
    lateinit var bluetooth: Bluetooth

    @Inject
    lateinit var sensorReadings: Flowable<List<SensorReading>>

    @Inject
    lateinit var sensorValues: Flowable<Map<String, Number>>

    @Inject
    lateinit var saveSensorReadingsUseCase: SaveSensorReadingsUseCase

    @Inject
    lateinit var sensorValuesToReadingsUseCase: SensorValuesToReadingsUseCase

    @Inject
    lateinit var logReadingsUseCase: LogReadingUseCase

    @Inject
    lateinit var inputToOutcomesUseCase: InputToOutcomesUseCase

    @Inject
    lateinit var getAllEnabledGooglePublishUseCase: GetAllEnabledGooglePublishUseCase

    @Inject
    lateinit var getAllEnabledRESTPublishUseCase: GetAllEnabledRESTPublishUseCase

    @Inject
    lateinit var updateRESTPublishUserCase: UpdateRESTPublishUserCase

    @Inject
    lateinit var updateGooglePublishUseCase: UpdateGooglePublishUseCase

    @Inject
    lateinit var getDevicesThatConnectedWithGooglePublishUseCase: GetDevicesThatConnectedWithGooglePublishUseCase

    @Inject
    lateinit var getDevicesThatConnectedWithRESTPublishUseCase: GetDevicesThatConnectedWithRESTPublishUseCase

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
    lateinit var sensorReadingToInputUseCase: SensorReadingToInputUseCase

    private var closeConnectionUseCase: CloseConnectionUseCase? = null
    private var publishReadingsUseCase: PublishReadingsUseCase? = null
    private var publishers: List<Publisher>? = null

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
        sensorReadings.subscribe {
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
        sensorReadings
            .concatMap { sensorReadingToInputUseCase.execute(it).toFlowable() }
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
        sensorReadings.subscribe {
            saveSensorReadingsUseCase.execute(it)
        }
    }

    private fun startLogging() {
        sensorValues.concatMap { sensorValuesToReadingsUseCase.execute(it).toFlowable() }
            .subscribe {
                logReadingsUseCase.execute(it)
            }
    }

    private fun initPublishers() {
        Single.merge(
            getAllEnabledGooglePublishUseCase.execute(),
            getAllEnabledRESTPublishUseCase.execute()
        )
            .subscribeOn(Schedulers.io())
            .flatMapIterable { it }
            .flatMap<Pair<BasePublish, List<Device>>>({
                when (it) {
                    is GooglePublish -> Flowable.just(it).zipWith(
                        getDevicesThatConnectedWithGooglePublishUseCase.execute(it.id)
                            .toFlowable()
                    )
                    is RESTPublish -> Flowable.just(it).zipWith(
                        getDevicesThatConnectedWithRESTPublishUseCase.execute(it.id)
                            .toFlowable()
                    )
                    else -> throw IllegalArgumentException("Illegal argument passed")
                }
            })
            .map {
                if (it.second.isNotEmpty()) {
                    when (it.first) {
                        is GooglePublish -> GoogleCloudPublisher(
                            this,
                            it.first as GooglePublish,
                            it.second
                        )
                        is RESTPublish -> RESTPublisher(
                            it.first as RESTPublish,
                            it.second
                        )
                        else -> {
                            EmptyPublisher()
                        }
                    }
                } else {
                    EmptyPublisher()
                }
            }//This line is used to eliminate unregistered types or nulls.
            .filter { it !is EmptyPublisher }
            .toList()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
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