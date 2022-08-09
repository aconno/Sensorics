package com.aconno.sensorics

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aconno.sensorics.dagger.worker.ChildWorkerFactory
import com.aconno.sensorics.domain.ConfigListManager
import com.aconno.sensorics.domain.FormatListManager
import com.aconno.sensorics.domain.interactor.sync.SyncUseCase
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.*
import timber.log.Timber

class SyncConfigurationWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted private val params: WorkerParameters,
    private val syncUseCase: SyncUseCase,
    private val configListManager: ConfigListManager,
    private val formatListManager: FormatListManager
) : Worker(appContext, params) {

    lateinit var job: Deferred<Boolean>

    override fun doWork(): Result {
        Timber.i("Job scheduler started")

        GlobalScope.launch(Dispatchers.Main) {

            //            delay(1 * 60 * 1000)

            job = GlobalScope.async {
                syncUseCase.execute()
            }

            broadcastUpdatingFinished()
        }

        return Result.success()
    }

    private suspend fun broadcastUpdatingFinished() {
        val shouldUpdate = job.await()

        if (shouldUpdate) {
            configListManager.isDirty = true
            formatListManager.isDirty = true
            Timber.d("Configs are dirty..")
        }
    }

    @AssistedInject.Factory
    interface Factory : ChildWorkerFactory
}