package com.aconno.acnsensa.dagger

import android.bluetooth.BluetoothAdapter
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.data.repository.InMemoryRepositoryImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothPermission
import com.aconno.acnsensa.device.bluetooth.BluetoothPermissionImpl
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.interactor.bluetooth.DeserializeScanResultUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.repository.InMemoryRepository
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import javax.inject.Singleton

/**
 * @author aconno
 */
@Module
class AppModule(private val acnSensaApplication: AcnSensaApplication) {

    @Provides
    @Singleton
    fun provideBluetooth(
        bluetoothAdapter: BluetoothAdapter,
        bluetoothPermission: BluetoothPermission
    ): Bluetooth = BluetoothImpl(bluetoothAdapter, bluetoothPermission)

    @Provides
    @Singleton
    fun provideBluetoothAdapter() = BluetoothAdapter.getDefaultAdapter()

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
    fun provideInMemoryRepository(): InMemoryRepository = InMemoryRepositoryImpl()
}