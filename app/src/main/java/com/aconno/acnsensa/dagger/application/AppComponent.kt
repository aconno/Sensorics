package com.aconno.acnsensa.dagger.application

import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
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

    fun actionsRepository(): ActionsRepository

    fun notificationDisplay(): NotificationDisplay

    fun vibrator(): Vibrator

    fun smsSender(): SmsSender

    fun bluetoothStateReceiver(): BluetoothStateReceiver

    fun localBroadcastManager(): LocalBroadcastManager

    //Classes which can accept injected dependencies.
}