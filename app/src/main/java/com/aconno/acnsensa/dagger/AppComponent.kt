package com.aconno.acnsensa.dagger

import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import dagger.Component
import io.reactivex.Flowable
import javax.inject.Singleton

/**
 * @author aconno
 */
@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {
    //Exposed dependencies for child components.
    fun acnSensaApplication(): AcnSensaApplication

    fun bluetooth(): Bluetooth

    fun inMemoryRepository(): InMemoryRepository

    fun sensorValues(): Flowable<Map<String, Number>>

    //Classes which can accept injected dependencies.
}