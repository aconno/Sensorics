package com.aconno.acnsensa.dagger

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.domain.advertisement.AdvertisementMatcher
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.GetSensorValuesUseCase
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModel
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModelFactory
import com.aconno.acnsensa.viewmodel.SensorListViewModel
import com.aconno.acnsensa.viewmodel.SensorListViewModelFactory
import dagger.Module
import dagger.Provides

/**
 * @author aconno
 */
@Module
class MainActivityModule(private val mainActivity: MainActivity) {

    @Provides
    @MainActivityScope
    fun provideAdvertisementMatcher() = AdvertisementMatcher()

    @Provides
    @MainActivityScope
    fun provideFilterAdvertisementUseCase(advertisementMatcher: AdvertisementMatcher) =
        FilterAdvertisementsUseCase(advertisementMatcher)

    @Provides
    @MainActivityScope
    fun provideSensorValuesUseCase(advertisementMatcher: AdvertisementMatcher) =
        GetSensorValuesUseCase(advertisementMatcher)

    @Provides
    @MainActivityScope
    fun provideSensorListViewModel(sensorListViewModelFactory: SensorListViewModelFactory) =
        ViewModelProviders.of(mainActivity, sensorListViewModelFactory)
            .get(SensorListViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideSensorListViewModelFactory(
        bluetooth: Bluetooth,
        filterAdvertisementsUseCase: FilterAdvertisementsUseCase,
        sensorValuesUseCase: GetSensorValuesUseCase
    ) = SensorListViewModelFactory(
        bluetooth,
        filterAdvertisementsUseCase,
        sensorValuesUseCase
    )

    @Provides
    @MainActivityScope
    fun provideBluetoothScanningViewModel(
        bluetoothScanningViewModelFactory: BluetoothScanningViewModelFactory
    ) = ViewModelProviders.of(mainActivity, bluetoothScanningViewModelFactory)
        .get(BluetoothScanningViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideBluetoothScanningViewModelFactory(
        bluetooth: Bluetooth,
        acnSensaApplication: AcnSensaApplication
    ) = BluetoothScanningViewModelFactory(
        bluetooth,
        acnSensaApplication
    )

    @Provides
    @MainActivityScope
    fun provideMainActivity() = mainActivity
}