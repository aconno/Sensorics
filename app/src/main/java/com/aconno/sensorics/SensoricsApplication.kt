package com.aconno.sensorics

import android.app.Activity
import android.app.Application
import android.app.Service
import androidx.work.Configuration
import androidx.work.WorkManager
import com.aconno.sensorics.dagger.application.DaggerAppComponent
import com.aconno.sensorics.dagger.worker.GeneralWorkerFactory
import com.crashlytics.android.Crashlytics
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import io.fabric.sdk.android.Fabric
import timber.log.Timber
import javax.inject.Inject

class SensoricsApplication : Application(), HasActivityInjector, HasServiceInjector {

    companion object {
        private const val DEV_BUILD_FLAVOR = "dev"
    }

    @Inject
    lateinit var workerFactory: GeneralWorkerFactory

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.builder().create(this).inject(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        @Suppress("ConstantConditionIf")
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Fabric.with(this, Crashlytics())

        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )

        AndroidThreeTen.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingActivityInjector
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return dispatchingServiceInjector
    }
}
