package com.aconno.sensorics.dagger.application

import android.support.v4.content.LocalBroadcastManager
import com.aconno.sensorics.BluetoothStateReceiver
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.notification.IntentProvider
import com.aconno.sensorics.domain.SmsSender
import com.aconno.sensorics.domain.Vibrator
import com.aconno.sensorics.domain.actions.ActionsRepository
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.ifttt.*
import com.aconno.sensorics.domain.interactor.convert.ReadingToInputUseCase
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesMaybeUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.repository.DeviceRepository
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.ui.dialogs.SavedDevicesDialog
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import dagger.Component
import io.reactivex.Flowable
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent {

    fun sensoricsApplication(): SensoricsApplication

    fun bluetooth(): Bluetooth

    fun inMemoryRepository(): InMemoryRepository

    fun scannedDevice(): Flowable<ScanDevice>

    fun savedDevices(): Flowable<List<Device>>

    fun filterByMacUseCase(): FilterByMacUseCase

    fun googlePublishRepository(): GooglePublishRepository

    fun restPublishRepository(): RESTPublishRepository

    fun mqttPublishRepository(): MqttPublishRepository

    fun actionsRepository(): ActionsRepository

    fun notificationDisplay(): NotificationDisplay

    fun vibrator(): Vibrator

    fun smsSender(): SmsSender

    fun textToSpeechPlayer(): TextToSpeechPlayer

    fun bluetoothStateReceiver(): BluetoothStateReceiver

    fun localBroadcastManager(): LocalBroadcastManager

    fun intentProvider(): IntentProvider

    fun deviceRepository(): DeviceRepository

    fun readingToInputUseCase(): ReadingToInputUseCase

    fun inject(scannedDevicesDialog: ScannedDevicesDialog)

    fun inject(savedDevicesDialog: SavedDevicesDialog)

    fun publishDeviceJoinRepository(): PublishDeviceJoinRepository

    fun readingsStream(): Flowable<List<Reading>>

    fun getSavedDevicesMaybeUseCase(): GetSavedDevicesMaybeUseCase

    fun formatMatcher(): FormatMatcher
}