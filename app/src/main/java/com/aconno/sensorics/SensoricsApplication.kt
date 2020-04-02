package com.aconno.sensorics

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import com.aconno.sensorics.dagger.application.DaggerAppComponent
import com.aconno.sensorics.dagger.worker.GeneralWorkerFactory
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import io.fabric.sdk.android.Fabric
import io.tempo.Tempo
import io.tempo.time_sources.SlackSntpTimeSource
import timber.log.Timber
import javax.inject.Inject

class SensoricsApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var workerFactory: GeneralWorkerFactory

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>


    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder().create(this).inject(this)

        @Suppress("ConstantConditionIf")
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initializeFabric()

        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )

        AndroidThreeTen.init(this)

        Tempo.initialize(this, timeSources = listOf(SlackSntpTimeSource()))
    }

    private fun initializeFabric() {
        Crashlytics.Builder()
            .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
            .build()
            .also { crashlytics ->
                Fabric.with(this, crashlytics)
            }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}
