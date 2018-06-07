package com.aconno.acnsensa.dagger.application

import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.device.notification.IntentProvider
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.*
import com.aconno.acnsensa.domain.interactor.bluetooth.DeserializeScanResultUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterByMacAddressUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.ui.dialogs.SavedDevicesDialog
import com.aconno.acnsensa.ui.dialogs.ScannedDevicesDialog
import dagger.Component
import io.reactivex.Flowable
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun acnSensaApplication(): AcnSensaApplication

    fun bluetooth(): Bluetooth

    fun inMemoryRepository(): InMemoryRepository

    fun sensorValues(): Flowable<Map<String, Number>>

    fun sensorReadings(): Flowable<List<SensorReading>>

    fun scannedDevices(): Flowable<Device>

    fun savedDevices(): Flowable<List<Device>>

    fun scanResults(): Flowable<ScanResult>

    fun filterAdvertisementUseCase(): FilterAdvertisementsUseCase

    fun filterByMacAddressUseCase(): FilterByMacAddressUseCase

    fun deserializeScanResultUseCase(): DeserializeScanResultUseCase

    fun googlePublishRepository(): GooglePublishRepository

    fun restPublishRepository(): RESTPublishRepository

    fun actionsRepository(): ActionsRepository

    fun notificationDisplay(): NotificationDisplay

    fun vibrator(): Vibrator

    fun smsSender(): SmsSender

    fun textToSpeechPlayer(): TextToSpeechPlayer

    fun bluetoothStateReceiver(): BluetoothStateReceiver

    fun localBroadcastManager(): LocalBroadcastManager

    fun intentProvider(): IntentProvider

    fun deviceRepository(): DeviceRepository

    fun inject(scannedDevicesDialog: ScannedDevicesDialog)

    fun inject(savedDevicesDialog: SavedDevicesDialog)
}