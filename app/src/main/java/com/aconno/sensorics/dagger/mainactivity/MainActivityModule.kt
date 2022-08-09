package com.aconno.sensorics.dagger.mainactivity

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.dagger.compositescan.CompositeScanResultsModule
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.interactor.repository.*
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
import javax.inject.Named


@Module(
    includes = [
        CompositeScanResultsModule::class
    ]
)
class MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideSensorListViewModel(
        mainActivity: MainActivity,
        sensorListViewModelFactory: SensorListViewModelFactory
    ) = ViewModelProvider(mainActivity, sensorListViewModelFactory)
        .get(SensorListViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideSensorListViewModelFactory(
        @Named("composite") readingsStream: Flowable<List<Reading>>,
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
    ) = ViewModelProvider(mainActivity, readingListViewModelFactory)
        .get(ReadingListViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideReadingListViewModelFactory(
        @Named("composite") readingsStream: Flowable<List<Reading>>,
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
    ) = ViewModelProvider(mainActivity, bluetoothScanningViewModelFactory)
        .get(BluetoothScanningViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideMqttVirtualScanningViewModel(
        mainActivity: MainActivity,
        mqttVirtualScanningViewModelFactory: MqttVirtualScanningViewModelFactory
    ) = ViewModelProvider(mainActivity, mqttVirtualScanningViewModelFactory)
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
    fun provideBluetoothViewModelFactory(
        bluetooth: Bluetooth
    ) = BluetoothViewModelFactory(bluetooth)

    @Provides
    @MainActivityScope
    fun provideBluetoothViewModel(
        mainActivity: MainActivity, bluetoothViewModelFactory: BluetoothViewModelFactory
    ) = ViewModelProvider(
        mainActivity,
        bluetoothViewModelFactory
    ).get(BluetoothViewModel::class.java)


    @Provides
    @MainActivityScope
    fun provideUseCasesViewModel(
        mainActivity: MainActivity,
        useCasesViewModelFactory: UseCasesViewModelFactory
    ) = ViewModelProvider(mainActivity, useCasesViewModelFactory)
        .get(UseCasesViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideDeviceListViewModel(
        mainActivity: MainActivity,
        deviceListViewModelFactory: DeviceListViewModelFactory
    ) = ViewModelProvider(mainActivity, deviceListViewModelFactory)
        .get(DeviceViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideDeviceGroupViewModel(
        mainActivity: MainActivity,
        deviceGroupViewModelFactory: DeviceGroupViewModelFactory
    ): DeviceGroupViewModel {
        return ViewModelProvider(mainActivity, deviceGroupViewModelFactory)
            .get(DeviceGroupViewModel::class.java)
    }

    @Provides
    @MainActivityScope
    fun provideDeviceGroupViewModelFactory(
        saveDeviceGroupUseCase: SaveDeviceGroupUseCase,
        getSavedDeviceGroupsUseCase: GetSavedDeviceGroupsUseCase,
        deleteDeviceGroupsUseCase: DeleteDeviceGroupUseCase,
        updateDeviceGroupsUseCase: UpdateDeviceGroupUseCase,
        saveDeviceGroupDeviceJoinUseCase: SaveDeviceGroupDeviceJoinUseCase,
        deleteDeviceGroupDeviceJoinUseCase: DeleteDeviceGroupDeviceJoinUseCase,
        getDevicesInDeviceGroupUseCase: GetDevicesInDeviceGroupUseCase,
        getDevicesBelongingSomeDeviceGroupUseCase: GetDevicesBelongingSomeDeviceGroupUseCase
    ): DeviceGroupViewModelFactory {
        return DeviceGroupViewModelFactory(saveDeviceGroupUseCase,
            getSavedDeviceGroupsUseCase,deleteDeviceGroupsUseCase,
            updateDeviceGroupsUseCase, saveDeviceGroupDeviceJoinUseCase,
            deleteDeviceGroupDeviceJoinUseCase, getDevicesInDeviceGroupUseCase,
            getDevicesBelongingSomeDeviceGroupUseCase)
    }

    @Provides
    @MainActivityScope
    fun provideDeviceListViewModelFactory(
        @Named("composite") scanDeviceStream: Flowable<ScanDevice>,
        getSavedDevicesUseCase: GetSavedDevicesUseCase,
        saveDeviceUseCase: SaveDeviceUseCase,
        updateDeviceUseCase: UpdateDeviceUseCase,
        deleteDeviceUseCase: DeleteDeviceUseCase,
        getIconUseCase: GetIconUseCase
    ): DeviceListViewModelFactory {
        val deviceStream = scanDeviceStream.map { it.device }
        return DeviceListViewModelFactory(
            deviceStream,
            getSavedDevicesUseCase,
            saveDeviceUseCase,
            updateDeviceUseCase,
            deleteDeviceUseCase,
            getIconUseCase
        )
    }

    @Provides
    @MainActivityScope
    fun provideUseCasesViewModelFactory(
        @Named("composite") readingsStream: Flowable<List<Reading>>,
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
    ) = ViewModelProvider(mainActivity, useCasesViewModelFactory)
        .get(DashboardViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideDashboardViewModelFactory(
        @Named("composite") readingsStream: Flowable<List<Reading>>
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
    ) = ViewModelProvider(
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
    ) = ViewModelProvider(mainActivity, mainResourceViewModelFactory)
        .get(MainResourceViewModel::class.java)


}
