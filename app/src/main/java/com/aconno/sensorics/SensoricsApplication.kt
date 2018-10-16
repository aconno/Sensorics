package com.aconno.sensorics

import android.annotation.SuppressLint
import android.app.Application
import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.dagger.application.AppModule
import com.aconno.sensorics.dagger.application.DaggerAppComponent
import com.crashlytics.android.Crashlytics
import com.squareup.leakcanary.LeakCanary
import io.fabric.sdk.android.Fabric
import timber.log.Timber

class SensoricsApplication : Application() {

    lateinit var appComponent: AppComponent

    @SuppressLint("CheckResult")
    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())
        Fabric.with(this, Crashlytics())

        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}