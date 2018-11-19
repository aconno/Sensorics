package com.aconno.sensorics.device.sync

import android.app.job.JobParameters
import android.app.job.JobService
import timber.log.Timber

class SyncConfigurationService : JobService() {
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