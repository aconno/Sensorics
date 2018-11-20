package com.aconno.sensorics

import android.app.job.JobParameters
import android.app.job.JobService
import com.aconno.sensorics.domain.ConfigListManager
import com.aconno.sensorics.domain.FormatListManager
import com.aconno.sensorics.domain.interactor.sync.SyncUseCase
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class SyncConfigurationService : JobService() {

    @Inject
    lateinit var syncUseCase: SyncUseCase

    @Inject
    lateinit var configListManager: ConfigListManager

    @Inject
    lateinit var formatListManager: FormatListManager

    lateinit var job: Deferred<Boolean>

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        Timber.i("Job scheduler started")

        GlobalScope.launch(Dispatchers.Main) {

            delay(1 * 60 * 1000)

            job = GlobalScope.async {
                syncUseCase.execute()
            }

            broadcastUpdatingFinished()
        }
        return false
    }

    private suspend fun broadcastUpdatingFinished() {
        val shouldUpdate = job.await()

        if (shouldUpdate) {
            configListManager.isDirty = true
            formatListManager.isDirty = true
            Timber.d("Configs are dirty..")
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()

        Timber.i("Job scheduler stopped")
        return false
    }
}