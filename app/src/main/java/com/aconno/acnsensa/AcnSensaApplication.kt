package com.aconno.acnsensa

import android.app.Application
import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.dagger.application.AppModule
import com.aconno.acnsensa.dagger.application.DaggerAppComponent
import com.aconno.acnsensa.model.mapper.AdvertisementFormatMapper
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

/**
 * @author aconno
 */
class AcnSensaApplication : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())

        val mapper = AdvertisementFormatMapper()
        val reader = AdvertisementFormatReader()
        reader.readFlowable(this)
            .subscribe {
                appComponent = DaggerAppComponent
                    .builder()
                    .appModule(AppModule(this, it.map { mapper.toAdvertisementFormat(it) }))
                    .build()
            }
    }
}