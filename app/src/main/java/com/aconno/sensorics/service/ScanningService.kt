package com.aconno.sensorics.service

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.domain.ifttt.outcome.RunOutcomeUseCase
import com.aconno.sensorics.domain.interactor.LogReadingUseCase
import com.aconno.sensorics.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.sensorics.domain.interactor.ifttt.InputToOutcomesUseCase
import com.aconno.sensorics.domain.interactor.repository.*
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.repository.SyncRepository
import dagger.android.DaggerService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

abstract class ScanningService : DaggerService() {
    abstract val readings: Flowable<List<Reading>>

    @Inject
    lateinit var syncRepository: SyncRepository

    @Inject
    lateinit var localBroadcastManager: LocalBroadcastManager

    @Inject
    lateinit var saveSensorReadingsUseCase: SaveSensorReadingsUseCase

    @Inject
    lateinit var logReadingsUseCase: LogReadingUseCase

    @Inject
    lateinit var inputToOutcomesUseCase: InputToOutcomesUseCase

    @Inject
    lateinit var getDevicesConnectedWithPublishUseCase: GetDevicesConnectedWithPublishUseCase

    @Inject
    lateinit var getRestHeadersByIdUseCase: GetRestHeadersByIdUseCase

    @Inject
    lateinit var getRestHttpGetParamsByIdUseCase: GetRestHttpGetParamsByIdUseCase

    @Inject
    lateinit var runOutcomeUseCase: RunOutcomeUseCase

    @Inject
    lateinit var readingToInputUseCase: ReadingToInputUseCase

    @Inject
    lateinit var getSavedDevicesUseCase: GetSavedDevicesUseCase

    @Inject
    lateinit var receiver: BroadcastReceiver

    @Inject
    lateinit var filter: IntentFilter

    @Inject
    lateinit var notification: Notification

    protected val disposables = CompositeDisposable()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        localBroadcastManager.registerReceiver(receiver, filter)

        startForeground(1, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    protected fun handleInputsForActions() {
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

    private var recordReadingsDisposable: Disposable? = null

    protected fun startRecording() {
        recordReadingsDisposable = readings.subscribe {
            saveSensorReadingsUseCase.execute(it)
        }
    }

    protected fun stopRecording() {
        recordReadingsDisposable?.dispose()
    }

    protected fun startLogging() {
        disposables.add(
            readings.subscribe {
                logReadingsUseCase.execute(it)
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}