package com.aconno.acnsensa.dagger.application

import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.device.notification.IntentProvider
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.ActionsRepository
import com.aconno.acnsensa.domain.ifttt.GooglePublishRepository
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
import com.aconno.acnsensa.domain.ifttt.TextToSpeechPlayer
import com.aconno.acnsensa.domain.interactor.bluetooth.DeserializeScanResultUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterByMacAddressUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
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

    fun beacons(): Flowable<Device>

    fun scanResults(): Flowable<ScanResult>

    fun filterAdvertisementUseCase(): FilterAdvertisementsUseCase

    fun filterByMacAddressUseCase(): FilterByMacAddressUseCase

    fun deserializeScanResultUseCase(): DeserializeScanResultUseCase

    fun googlePublishRepository(): GooglePublishRepository

    fun actionsRepository(): ActionsRepository

    fun notificationDisplay(): NotificationDisplay

    fun vibrator(): Vibrator

    fun smsSender(): SmsSender

    fun textToSpeechPlayer(): TextToSpeechPlayer

    fun bluetoothStateReceiver(): BluetoothStateReceiver

    fun localBroadcastManager(): LocalBroadcastManager

    fun intentProvider(): IntentProvider

    //Classes which can accept injected dependencies.
}