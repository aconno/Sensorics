package com.aconno.acnsensa

import android.app.Application
import com.aconno.acnsensa.dagger.AppComponent
import com.aconno.acnsensa.dagger.AppModule
import com.aconno.acnsensa.dagger.DaggerAppComponent

/**
 * @author aconno
 */
class AcnSensaApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }
}