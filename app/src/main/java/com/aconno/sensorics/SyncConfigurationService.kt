package com.aconno.sensorics

import android.app.job.JobParameters
import android.app.job.JobService
import com.aconno.sensorics.domain.interactor.sync.SyncUseCase
import dagger.android.AndroidInjection
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class SyncConfigurationService : JobService() {

    @Inject
    lateinit var syncUseCase: SyncUseCase

    lateinit var job: Deferred<Boolean>

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onStartJob(params: JobParameters?): Boolean {

        GlobalScope.launch(Dispatchers.Main) {
            job = GlobalScope.async {
                syncUseCase.execute()
            }

            broadcastUpdatingFinished()
        }

        Timber.i("Job scheduler started")
        return false
    }

    private suspend fun broadcastUpdatingFinished() {
        val shouldUpdate = job.await()

        if (shouldUpdate) {
            Timber.d("Update..")
            //TODO Broadcast Update Finished
        }
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.cancel()

        Timber.i("Job scheduler stopped")
        return false
    }
}