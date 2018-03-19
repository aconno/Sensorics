package com.aconno.acnsensa.dagger

import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.domain.Bluetooth
import dagger.Component
import io.reactivex.Flowable
import javax.inject.Singleton

/**
 * @author aconno
 */
@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    //Here is where I should expose dependencies for child components.
    fun acnSensaApplication(): AcnSensaApplication

    fun bluetooth(): Bluetooth

    fun sensorValues(): Flowable<Map<String, Number>>

    //Here is where the code defines what classes can accept injected dependencies.
}