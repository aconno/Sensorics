package com.aconno.acnsensa

import android.app.Notification
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.dagger.bluetoothscanning.BluetoothScanningServiceComponent
import com.aconno.acnsensa.dagger.bluetoothscanning.BluetoothScanningServiceModule
import com.aconno.acnsensa.dagger.bluetoothscanning.DaggerBluetoothScanningServiceComponent
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.ifttt.outcome.RunOutcomeUseCase
import com.aconno.acnsensa.domain.ifttt.outcome.VibrationOutcomeExecutor.Companion.running
import com.aconno.acnsensa.domain.interactor.LogReadingUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.acnsensa.domain.interactor.ifttt.ReadingToInputUseCase
import com.aconno.acnsensa.domain.interactor.mqtt.CloseConnectionUseCase
import com.aconno.acnsensa.domain.interactor.mqtt.PublishReadingsUseCase
import com.aconno.acnsensa.domain.interactor.repository.RecordSensorValuesUseCase
import com.aconno.acnsensa.domain.interactor.repository.SensorValuesToReadingsUseCase
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * @author aconno
 */
class BluetoothScanningService : Service() {

    @Inject
    lateinit var bluetooth: Bluetooth

    @Inject
    lateinit var sensorValues: Flowable<Map<String, Number>>

    @Inject
    lateinit var recordUseCase: RecordSensorValuesUseCase

    @Inject
    lateinit var sensorValuesToReadingsUseCase: SensorValuesToReadingsUseCase

    @Inject
    lateinit var logReadingsUseCase: LogReadingUseCase

    @Inject
    lateinit var publishReadingsUseCase: PublishReadingsUseCase

    @Inject
    lateinit var closeConnectionUseCase: CloseConnectionUseCase

    @Inject
    lateinit var readingToInputUseCase: ReadingToInputUseCase

    @Inject
    lateinit var inputToOutcomesUseCase: InputToOutcomesUseCase

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

    private val bluetoothScanningServiceComponent: BluetoothScanningServiceComponent by lazy {
        val acnSensaApplication: AcnSensaApplication? = application as? AcnSensaApplication
        DaggerBluetoothScanningServiceComponent.builder()
            .appComponent(acnSensaApplication?.appComponent)
            .bluetoothScanningServiceModule(
                BluetoothScanningServiceModule(
                    this
                )
            )
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

        bluetooth.startScanning()
        running = true
        startRecording()
        startLogging()
        startSyncing()
        handleInputsForActions()
        return START_STICKY
    }

    private fun startSyncing() {
        sensorValues.concatMap { sensorValuesToReadingsUseCase.execute(it).toFlowable() }
            .subscribe {
                //Publish when Google Cloud Integration Enabled
                if (isGoogleCloudIntegrationEnabled() && isAllDataProvidedByUser()) {
                    publishReadingsUseCase.execute(it)
                }
            }
    }

    private fun isAllDataProvidedByUser(): Boolean {
        //TODO Can be inject by Dagger
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (preferences != null) {
            if (preferences.contains("region_preference")
                && preferences.contains("deviceregistry_preference")
                && preferences.contains("device_preference")
                && preferences.contains("privatekey_preference")
            ) {
                val projectidPreference = preferences.getString("projectid_preference", "")
                val regionPreference = preferences.getString("region_preference", "")
                val deviceregistryPreference =
                    preferences.getString("deviceregistry_preference", "")
                val devicePreference = preferences.getString("device_preference", "")
                val privatekeyPreference = preferences.getString("privatekey_preference", "")

                if (projectidPreference.isEmpty()
                    || regionPreference.isEmpty()
                    || deviceregistryPreference.isEmpty()
                    || devicePreference.isEmpty()
                    || privatekeyPreference.isEmpty()
                ) {
                    return false
                }
            } else {
                return false
            }
        } else {
            throw NullPointerException("SharedPreferences is null")
        }

        return true
    }

    /**
     * Look Settings
     */
    private fun isGoogleCloudIntegrationEnabled(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs?.let {
            return it.contains("gcloud_switch_preference") && it.getBoolean(
                "gcloud_switch_preference",
                false
            )
        }

        return false
    }

    private fun handleInputsForActions() {
        sensorValues
            .concatMap { sensorValuesToReadingsUseCase.execute(it).toFlowable() }
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
        closeConnectionUseCase.execute()
        bluetooth.stopScanning()
        running = false
        stopSelf()
    }

    private fun startRecording() {
        sensorValues.concatMap { sensorValuesToReadingsUseCase.execute(it).toFlowable() }
            .subscribe {
                recordUseCase.execute(it)
            }
    }

    private fun startLogging() {
        sensorValues.concatMap { sensorValuesToReadingsUseCase.execute(it).toFlowable() }
            .subscribe {
                logReadingsUseCase.execute(it)
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