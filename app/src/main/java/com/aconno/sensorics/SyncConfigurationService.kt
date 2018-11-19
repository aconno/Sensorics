package com.aconno.sensorics

import android.app.job.JobParameters
import android.app.job.JobService
import dagger.android.AndroidInjection
import timber.log.Timber

class SyncConfigurationService : JobService() {

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        //println("Job stopped....")
        Timber.i("Job scheduler stopped")
        return false;
    }

    override fun onStartJob(params: JobParameters?): Boolean {

        Timber.i("Job scheduler started")
        return false;
    }

}