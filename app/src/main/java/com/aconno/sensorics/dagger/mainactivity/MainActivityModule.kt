package com.aconno.sensorics.dagger.mainactivity

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.BluetoothStateReceiver
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.permissons.PermissionActionFactory
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceUseCase
import com.aconno.sensorics.domain.interactor.repository.GetReadingsUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.sensorics.domain.interactor.resources.GetIconUseCase
import com.aconno.sensorics.domain.interactor.resources.GetMainResourceUseCase
import com.aconno.sensorics.domain.interactor.resources.GetUseCaseResourceUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.readings.ReadingListViewModel
import com.aconno.sensorics.ui.readings.ReadingListViewModelFactory
import com.aconno.sensorics.viewmodel.*
import com.aconno.sensorics.viewmodel.factory.*
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModel
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable


@Module
class MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideSensorListViewModel(
        mainActivity: MainActivity,
        sensorListViewModelFactory: SensorListViewModelFactory
    ) = ViewModelProviders.of(mainActivity, sensorListViewModelFactory)
        .get(SensorListViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideSensorListViewModelFactory(
        readingsStream: Flowable<List<Reading>>,
        filterByMacUseCase: FilterByMacUseCase
    ) = SensorListViewModelFactory(
        readingsStream,
        filterByMacUseCase
    )

    @Provides
    @MainActivityScope
    fun provideReadingListViewModel(
        mainActivity: MainActivity,
        readingListViewModelFactory: ReadingListViewModelFactory
    ) = ViewModelProviders.of(mainActivity, readingListViewModelFactory)
        .get(ReadingListViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideReadingListViewModelFactory(
        readingsStream: Flowable<List<Reading>>,
        filterByMacUseCase: FilterByMacUseCase
    ) = ReadingListViewModelFactory(
        readingsStream,
        filterByMacUseCase
    )

    @Provides
    @MainActivityScope
    fun provideBluetoothScanningViewModel(
        mainActivity: MainActivity,
        bluetoothScanningViewModelFactory: BluetoothScanningViewModelFactory
    ) = ViewModelProviders.of(mainActivity, bluetoothScanningViewModelFactory)
        .get(BluetoothScanningViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideMqttVirtualScanningViewModel(
        mainActivity: MainActivity,
        mqttVirtualScanningViewModelFactory: MqttVirtualScanningViewModelFactory
    ) = ViewModelProviders.of(mainActivity, mqttVirtualScanningViewModelFactory)
        .get(MqttVirtualScanningViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideBluetoothScanningViewModelFactory(
        bluetooth: Bluetooth,
        sensoricsApplication: SensoricsApplication
    ) = BluetoothScanningViewModelFactory(
        bluetooth,
        sensoricsApplication
    )

    @Provides
    @MainActivityScope
    fun provideMqttVirtualScanningViewModelFactory(
        sensoricsApplication: SensoricsApplication
    ) = MqttVirtualScanningViewModelFactory(
        sensoricsApplication
    )

    @Provides
    @MainActivityScope
    fun providePermissionsViewModel(mainActivity: MainActivity): PermissionViewModel {
        val permissionAction = PermissionActionFactory.getPermissionAction(mainActivity)
        return PermissionViewModel(permissionAction, mainActivity)
    }

    @Provides
    @MainActivityScope
    fun provideBluetoothViewModelFactory(
        mainActivity: MainActivity,
        bluetooth: Bluetooth,
        bluetoothStateReceiver: BluetoothStateReceiver
    ) = BluetoothViewModelFactory(bluetooth, bluetoothStateReceiver, mainActivity.application)

    @Provides
    @MainActivityScope
    fun provideBluetoothViewModel(
        mainActivity: MainActivity, bluetoothViewModelFactory: BluetoothViewModelFactory
    ) = ViewModelProviders.of(
        mainActivity,
        bluetoothViewModelFactory
    ).get(BluetoothViewModel::class.java)


    @Provides
    @MainActivityScope
    fun provideUseCasesViewModel(
        mainActivity: MainActivity,
        useCasesViewModelFactory: UseCasesViewModelFactory
    ) = ViewModelProviders.of(mainActivity, useCasesViewModelFactory)
        .get(UseCasesViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideDeviceListViewModel(
        mainActivity: MainActivity,
        deviceListViewModelFactory: DeviceListViewModelFactory
    ): DeviceViewModel {
        return ViewModelProviders.of(mainActivity, deviceListViewModelFactory)
            .get(DeviceViewModel::class.java)
    }

    @Provides
    @MainActivityScope
    fun provideDeviceListViewModelFactory(
        scanDeviceStream: Flowable<ScanDevice>,
        getSavedDevicesUseCase: GetSavedDevicesUseCase,
        saveDeviceUseCase: SaveDeviceUseCase,
        deleteDeviceUseCase: DeleteDeviceUseCase,
        getIconUseCase: GetIconUseCase
    ): DeviceListViewModelFactory {
        val deviceStream = scanDeviceStream.map { it.device }
        return DeviceListViewModelFactory(
            deviceStream,
            getSavedDevicesUseCase,
            saveDeviceUseCase,
            deleteDeviceUseCase,
            getIconUseCase
        )
    }

    @Provides
    @MainActivityScope
    fun provideUseCasesViewModelFactory(
        readingsStream: Flowable<List<Reading>>,
        filterByMacUseCase: FilterByMacUseCase,
        getUseCaseResourceUseCase: GetUseCaseResourceUseCase
    ) = UseCasesViewModelFactory(
        readingsStream,
        filterByMacUseCase,
        getUseCaseResourceUseCase
    )

    @Provides
    @MainActivityScope
    fun provideDashboardViewModel(
        mainActivity: MainActivity,
        useCasesViewModelFactory: DashboardViewModelFactory
    ) = ViewModelProviders.of(mainActivity, useCasesViewModelFactory)
        .get(DashboardViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideDashboardViewModelFactory(
        readingsStream: Flowable<List<Reading>>
    ) = DashboardViewModelFactory(
        readingsStream
    )

    @Provides
    @MainActivityScope
    fun provideLiveGraphViewModelFactory(
        getReadingsUseCase: GetReadingsUseCase,
        mainActivity: MainActivity
    ) = LiveGraphViewModelFactory(
        getReadingsUseCase,
        mainActivity.application
    )

    @Provides
    @MainActivityScope
    fun provideLiveGraphViewModel(
        liveGraphViewModelFactory: LiveGraphViewModelFactory,
        mainActivity: MainActivity
    ) = ViewModelProviders.of(
        mainActivity,
        liveGraphViewModelFactory
    ).get(LiveGraphViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideMainResourceViewModelFactory(
        getMainResourceUseCase: GetMainResourceUseCase
    ) = MainResourceViewModelFactory(getMainResourceUseCase)

    @Provides
    @MainActivityScope
    fun provideMainResourceViewModel(
        mainActivity: MainActivity,
        mainResourceViewModelFactory: MainResourceViewModelFactory
    ) = ViewModelProviders.of(mainActivity, mainResourceViewModelFactory)
        .get(MainResourceViewModel::class.java)


}
