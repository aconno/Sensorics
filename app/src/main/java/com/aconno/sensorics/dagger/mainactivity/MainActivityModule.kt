package com.aconno.sensorics.dagger.mainactivity

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.BluetoothStateReceiver
import com.aconno.sensorics.LocationStateReceiver
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.location.LocationStateListener
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
import com.aconno.sensorics.domain.repository.DeviceRepository
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.ui.MainActivity2
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
        mainActivity: MainActivity2,
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
        mainActivity: MainActivity2,
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
        mainActivity: MainActivity2,
        bluetoothScanningViewModelFactory: BluetoothScanningViewModelFactory
    ) = ViewModelProviders.of(mainActivity, bluetoothScanningViewModelFactory)
        .get(BluetoothScanningViewModel::class.java)

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
    fun provideBluetoothViewModelFactory(
        mainActivity: MainActivity2,
        bluetooth: Bluetooth,
        bluetoothStateReceiver: BluetoothStateReceiver
    ) = BluetoothViewModelFactory(bluetooth, bluetoothStateReceiver, mainActivity.application)

    @Provides
    @MainActivityScope
    fun provideBluetoothViewModel(
        mainActivity: MainActivity2, bluetoothViewModelFactory: BluetoothViewModelFactory
    ) = ViewModelProviders.of(
        mainActivity,
        bluetoothViewModelFactory
    ).get(BluetoothViewModel::class.java)

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
    fun provideDeleteDeviceUseCase(
        deviceRepository: DeviceRepository
    ): DeleteDeviceUseCase {
        return DeleteDeviceUseCase(deviceRepository)
    }

    @Provides
    @MainActivityScope
    fun provideUseCasesViewModel(
        mainActivity: MainActivity2,
        useCasesViewModelFactory: UseCasesViewModelFactory
    ) = ViewModelProviders.of(mainActivity, useCasesViewModelFactory)
        .get(UseCasesViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideDeviceListViewModel(
        mainActivity: MainActivity2,
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
        mainActivity: MainActivity2,
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
        mainActivity: MainActivity2
    ) = LiveGraphViewModelFactory(
        getReadingsUseCase,
        mainActivity.application
    )

    @Provides
    @MainActivityScope
    fun provideLiveGraphViewModel(
        liveGraphViewModelFactory: LiveGraphViewModelFactory,
        mainActivity: MainActivity2
    ) = ViewModelProviders.of(
        mainActivity,
        liveGraphViewModelFactory
    ).get(LiveGraphViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideGetSensorReadingsUseCase(
        inMemoryRepository: InMemoryRepository
    ) = GetReadingsUseCase(inMemoryRepository)

    @Provides
    @MainActivityScope
    fun provideMainResourceViewModelFactory(
        getMainResourceUseCase: GetMainResourceUseCase
    ) = MainResourceViewModelFactory(getMainResourceUseCase)

    @Provides
    @MainActivityScope
    fun provideMainResourceViewModel(
        mainActivity: MainActivity2,
        mainResourceViewModelFactory: MainResourceViewModelFactory
    ) = ViewModelProviders.of(mainActivity, mainResourceViewModelFactory)
        .get(MainResourceViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideLocationViewModelFactory(
        mainActivity: MainActivity2,
        locationStateReceiver: LocationStateReceiver,
        locationStateListener: LocationStateListener
    ) = LocationViewModelFactory(
        locationStateReceiver,
        locationStateListener,
        mainActivity.application
    )

    @Provides
    @MainActivityScope
    fun provideLocationViewModel(
        mainActivity: MainActivity2,
        locationViewModelFactory: LocationViewModelFactory
    ) = ViewModelProviders.of(
        mainActivity,
        locationViewModelFactory
    ).get(LocationViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideLocationStateReceiver(locationStateListener: LocationStateListener):
            LocationStateReceiver = LocationStateReceiver(locationStateListener)

    @Provides
    @MainActivityScope
    fun provideLocationStateListener(): LocationStateListener = LocationStateListener()
}
