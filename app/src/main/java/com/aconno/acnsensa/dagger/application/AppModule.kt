package com.aconno.acnsensa.dagger.application

import android.arch.persistence.room.Room
import android.bluetooth.BluetoothAdapter
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.IntentProviderImpl
import com.aconno.acnsensa.data.mapper.*
import com.aconno.acnsensa.data.repository.*
import com.aconno.acnsensa.data.repository.action.ActionsRepositoryImpl
import com.aconno.acnsensa.data.repository.devices.DeviceMapper
import com.aconno.acnsensa.data.repository.devices.DeviceRepositoryImpl
import com.aconno.acnsensa.data.repository.gpublish.GooglePublishRepositoryImpl
import com.aconno.acnsensa.data.repository.pdjoin.PublishDeviceJoinRepositoryImpl
import com.aconno.acnsensa.data.repository.rpublish.RESTPublishRepositoryImpl
import com.aconno.acnsensa.device.SmsSenderImpl
import com.aconno.acnsensa.device.TextToSpeechPlayerImpl
import com.aconno.acnsensa.device.VibratorImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothPermission
import com.aconno.acnsensa.device.bluetooth.BluetoothPermissionImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothStateListener
import com.aconno.acnsensa.device.notification.IntentProvider
import com.aconno.acnsensa.device.notification.NotificationDisplayImpl
import com.aconno.acnsensa.device.notification.NotificationFactory
import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.ifttt.*
import com.aconno.acnsensa.domain.interactor.bluetooth.DeserializeScanResultUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterByMacAddressUseCase
import com.aconno.acnsensa.domain.interactor.convert.ScanResultToSensorReadingsUseCase
import com.aconno.acnsensa.domain.interactor.convert.SensorReadingToInputUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.model.SensorReading
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import com.aconno.acnsensa.domain.scanning.Bluetooth
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Singleton

@Module
class AppModule(private val acnSensaApplication: AcnSensaApplication) {

    @Provides
    @Singleton
    fun provideLocalBroadcastManager() =
        LocalBroadcastManager.getInstance(acnSensaApplication.applicationContext)

    @Provides
    @Singleton
    fun provideBluetoothStateReceiver(bluetoothStateListener: BluetoothStateListener) =
        BluetoothStateReceiver(bluetoothStateListener)

    @Provides
    @Singleton
    fun provideBluetoothStateListener() = BluetoothStateListener()

    @Provides
    @Singleton
    fun provideBluetooth(
        bluetoothAdapter: BluetoothAdapter,
        bluetoothPermission: BluetoothPermission,
        bluetoothStateListener: BluetoothStateListener
    ): Bluetooth = BluetoothImpl(bluetoothAdapter, bluetoothPermission, bluetoothStateListener)

    @Provides
    @Singleton
    fun provideBluetoothAdapter(): BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @Provides
    @Singleton
    fun provideBluetoothPermission(): BluetoothPermission = BluetoothPermissionImpl()

    @Provides
    @Singleton
    fun provideAcnSensaApplication(): AcnSensaApplication = acnSensaApplication

    @Provides
    @Singleton
    fun provideAdvertisementMatcher() = AdvertisementMatcher()

    @Provides
    @Singleton
    fun provideFilterAdvertisementUseCase(advertisementMatcher: AdvertisementMatcher) =
        FilterAdvertisementsUseCase(advertisementMatcher)

    @Provides
    @Singleton
    fun provideFilterByMacAddressUseCase() = FilterByMacAddressUseCase()

    @Provides
    @Singleton
    fun provideScanResultToSensorReadingsUseCase(advertisementMatcher: AdvertisementMatcher) =
        ScanResultToSensorReadingsUseCase(advertisementMatcher)

    @Provides
    @Singleton
    fun provideSensorValuesUseCase(advertisementMatcher: AdvertisementMatcher) =
        DeserializeScanResultUseCase(advertisementMatcher)

    @Provides
    @Singleton
    fun provideSensorValuesFlowable(
        bluetooth: Bluetooth,
        filterAdvertisementsUseCase: FilterAdvertisementsUseCase,
        sensorValuesUseCase: DeserializeScanResultUseCase
    ): Flowable<Map<String, Number>> {
        val observable: Flowable<ScanResult> = bluetooth.getScanResults()
        return observable
            .concatMap { filterAdvertisementsUseCase.execute(it).toFlowable() }
            .concatMap { sensorValuesUseCase.execute(it).toFlowable() }
    }

    @Provides
    @Singleton
    fun provideScanResultsFlowable(
        bluetooth: Bluetooth
    ): Flowable<ScanResult> {
        return bluetooth.getScanResults()
    }

    @Provides
    @Singleton
    fun provideBeaconsFlowable(
        bluetooth: Bluetooth,
        filterAdvertisementsUseCase: FilterAdvertisementsUseCase
    ): Flowable<Device> {
        val observable: Flowable<ScanResult> = bluetooth.getScanResults()
        return observable
            .concatMap { filterAdvertisementsUseCase.execute(it).toFlowable() }
            .map { it.device }
    }

    @Provides
    @Singleton
    fun provideSensorReadingsFlowable(
        bluetooth: Bluetooth,
        filterAdvertisementsUseCase: FilterAdvertisementsUseCase,
        scanResultToSensorReadingsUseCase: ScanResultToSensorReadingsUseCase
    ): Flowable<List<SensorReading>> {
        return bluetooth.getScanResults()
            .concatMap { filterAdvertisementsUseCase.execute(it).toFlowable() }
            .concatMap { scanResultToSensorReadingsUseCase.execute(it).toFlowable() }
    }

    @Provides
    @Singleton
    fun provideInMemoryRepository(): InMemoryRepository = InMemoryRepositoryImpl()

    @Provides
    @Singleton
    fun provideVibrator(): Vibrator {
        return VibratorImpl(acnSensaApplication.applicationContext)
    }

    @Provides
    @Singleton
    fun provideSmsSender(): SmsSender {
        return SmsSenderImpl()
    }

    @Provides
    @Singleton
    fun provideTextToSpeechPlayer(): TextToSpeechPlayer {
        return TextToSpeechPlayerImpl(acnSensaApplication)
    }

    @Provides
    @Singleton
    fun provideActionsRepository(
        acnSensaDatabase: AcnSensaDatabase
    ): ActionsRepository {
        return ActionsRepositoryImpl(acnSensaDatabase.actionDao())
    }

    @Provides
    @Singleton
    fun provideGooglePublishRepository(
        acnSensaDatabase: AcnSensaDatabase,
        googlePublishEntityDataMapper: GooglePublishEntityDataMapper,
        googlePublishDataMapper: GooglePublishDataMapper
    ): GooglePublishRepository {
        return GooglePublishRepositoryImpl(
            acnSensaDatabase.googlePublishDao(),
            googlePublishEntityDataMapper,
            googlePublishDataMapper
        )
    }

    @Provides
    @Singleton
    fun provideRESTPublishRepository(
        acnSensaDatabase: AcnSensaDatabase,
        restPublishEntityDataMapper: RESTPublishEntityDataMapper,
        restPublishDataMapper: RESTPublishDataMapper,
        restHeaderDataMapper: RESTHeaderDataMapper
    ): RESTPublishRepository {
        return RESTPublishRepositoryImpl(
            acnSensaDatabase.restPublishDao(),
            restPublishEntityDataMapper,
            restPublishDataMapper,
            restHeaderDataMapper
        )
    }

    @Provides
    @Singleton
    fun provideAcnSensaDatabase(): AcnSensaDatabase {
        return Room.databaseBuilder(acnSensaApplication, AcnSensaDatabase::class.java, "AcnSensa")
            .fallbackToDestructiveMigration()
            .build()

    }

    @Provides
    @Singleton
    fun provideNotificationDisplay(intentProvider: IntentProvider): NotificationDisplay {
        return NotificationDisplayImpl(
            NotificationFactory(), intentProvider, acnSensaApplication
        )
    }

    @Provides
    @Singleton
    fun provideIntentProvider(): IntentProvider {
        return IntentProviderImpl()
    }

    @Provides
    @Singleton
    fun provideDeviceRepository(
        acnSensaDatabase: AcnSensaDatabase,
        deviceMapper: DeviceMapper
    ): DeviceRepository {
        return DeviceRepositoryImpl(acnSensaDatabase.deviceDao(), deviceMapper)
    }

    @Provides
    @Singleton
    fun provideGetSavedDevicesList(
        deviceRepository: DeviceRepository
    ): Flowable<List<Device>> {
        return GetSavedDevicesUseCase(deviceRepository).execute()
    }

    @Provides
    @Singleton
    fun provideSensorReadingToInputUseCase() = SensorReadingToInputUseCase()

    @Provides
    @Singleton
    fun providePublishDeviceJoinRepository(
        acnSensaDatabase: AcnSensaDatabase,
        deviceMapper: DeviceMapper,
        publishDeviceJoinJoinMapper: PublishPublishDeviceJoinJoinMapper
    ): PublishDeviceJoinRepository {
        return PublishDeviceJoinRepositoryImpl(
            acnSensaDatabase.publishDeviceJoinDao(),
            deviceMapper,
            publishDeviceJoinJoinMapper
        )
    }
}