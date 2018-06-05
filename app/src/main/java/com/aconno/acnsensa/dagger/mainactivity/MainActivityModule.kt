package com.aconno.acnsensa.dagger.mainactivity

import android.arch.lifecycle.ViewModelProviders
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.device.permissons.PermissionActionFactory
import com.aconno.acnsensa.domain.interactor.bluetooth.DeserializeScanResultUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterAdvertisementsUseCase
import com.aconno.acnsensa.domain.interactor.bluetooth.FilterByMacAddressUseCase
import com.aconno.acnsensa.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.acnsensa.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.acnsensa.domain.model.Device
import com.aconno.acnsensa.domain.model.ScanResult
import com.aconno.acnsensa.domain.repository.DeviceRepository
import com.aconno.acnsensa.domain.scanning.Bluetooth
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.viewmodel.*
import com.aconno.acnsensa.viewmodel.factory.*
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

@Module
class MainActivityModule(private val mainActivity: MainActivity) {

    @Provides
    @MainActivityScope
    fun provideSensorListViewModel(sensorListViewModelFactory: SensorListViewModelFactory) =
        ViewModelProviders.of(mainActivity, sensorListViewModelFactory)
            .get(SensorListViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideSensorListViewModelFactory(
        scanResults: Flowable<ScanResult>,
        filterAdvertisementsUseCase: FilterAdvertisementsUseCase,
        filterByMacAddressUseCase: FilterByMacAddressUseCase,
        deserializeScanResultUseCase: DeserializeScanResultUseCase
    ) = SensorListViewModelFactory(
        scanResults,
        filterAdvertisementsUseCase,
        filterByMacAddressUseCase,
        deserializeScanResultUseCase
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

    @Provides
    @MainActivityScope
    fun providePermissionsViewModel(): PermissionViewModel {
        val permissionAction = PermissionActionFactory.getPermissionAction(mainActivity)
        return PermissionViewModel(permissionAction, mainActivity)
    }

    @Provides
    @MainActivityScope
    fun provideBluetoothViewModelFactory(
        bluetooth: Bluetooth,
        bluetoothStateReceiver: BluetoothStateReceiver
    ) =
        BluetoothViewModelFactory(bluetooth, bluetoothStateReceiver, mainActivity.application)

    @Provides
    @MainActivityScope
    fun provideBluetoothViewModel(bluetoothViewModelFactory: BluetoothViewModelFactory) =
        ViewModelProviders.of(
            mainActivity,
            bluetoothViewModelFactory
        ).get(BluetoothViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideBeaconListViewModel(
        beaconListViewModelFactory: BeaconListViewModelFactory
    ) = ViewModelProviders.of(mainActivity, beaconListViewModelFactory)
        .get(BeaconListViewModel::class.java)


    @Provides
    @MainActivityScope
    fun provideBeaconListViewModelFactory(
        beacons: Flowable<Device>
    ) = BeaconListViewModelFactory(beacons)

    @Provides
    @MainActivityScope
    fun provideGetAllDevicesUseCase(
        deviceRepository: DeviceRepository
    ): GetSavedDevicesUseCase {
        return GetSavedDevicesUseCase(deviceRepository)
    }

    @Provides
    @MainActivityScope
    fun provideSaveDeviceUseCase(
        deviceRepository: DeviceRepository
    ): SaveDeviceUseCase {
        return SaveDeviceUseCase(deviceRepository)
    }

    @Provides
    @MainActivityScope
    fun provideDeviceListViewModelFactory(
        getSavedDevicesUseCase: GetSavedDevicesUseCase,
        saveDeviceUseCase: SaveDeviceUseCase
    ): DeviceListViewModelFactory {
        return DeviceListViewModelFactory(getSavedDevicesUseCase, saveDeviceUseCase)
    }

    @Provides
    @MainActivityScope
    fun provideDeviceListViewModel(
        deviceListViewModelFactory: DeviceListViewModelFactory
    ): DeviceViewModel {
        return ViewModelProviders.of(mainActivity, deviceListViewModelFactory)
            .get(DeviceViewModel::class.java)
    }
}