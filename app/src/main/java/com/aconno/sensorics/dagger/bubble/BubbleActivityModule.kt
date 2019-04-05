package com.aconno.sensorics.dagger.bubble

import android.arch.lifecycle.ViewModelProviders
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
import com.aconno.sensorics.domain.repository.DeviceRepository
import com.aconno.sensorics.domain.repository.InMemoryRepository
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.ui.bubble.BubbleActivity
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
class BubbleActivityModule {

    @Provides
    @BubbleActivityScope
    fun provideSensorListViewModel(
        bubbleActivity: BubbleActivity,
        sensorListViewModelFactory: SensorListViewModelFactory
    ) = ViewModelProviders.of(bubbleActivity, sensorListViewModelFactory)
        .get(SensorListViewModel::class.java)

    @Provides
    @BubbleActivityScope
    fun provideSensorListViewModelFactory(
        readingsStream: Flowable<List<Reading>>,
        filterByMacUseCase: FilterByMacUseCase
    ) = SensorListViewModelFactory(
        readingsStream,
        filterByMacUseCase
    )

    @Provides
    @BubbleActivityScope
    fun provideReadingListViewModel(
        bubbleActivity: BubbleActivity,
        readingListViewModelFactory: ReadingListViewModelFactory
    ) = ViewModelProviders.of(bubbleActivity, readingListViewModelFactory)
        .get(ReadingListViewModel::class.java)

    @Provides
    @BubbleActivityScope
    fun provideReadingListViewModelFactory(
        readingsStream: Flowable<List<Reading>>,
        filterByMacUseCase: FilterByMacUseCase
    ) = ReadingListViewModelFactory(
        readingsStream,
        filterByMacUseCase
    )

    @Provides
    @BubbleActivityScope
    fun provideBluetoothScanningViewModel(
        bubbleActivity: BubbleActivity,
        bluetoothScanningViewModelFactory: BluetoothScanningViewModelFactory
    ) = ViewModelProviders.of(bubbleActivity, bluetoothScanningViewModelFactory)
        .get(BluetoothScanningViewModel::class.java)

    @Provides
    @BubbleActivityScope
    fun provideBluetoothScanningViewModelFactory(
        bluetooth: Bluetooth,
        sensoricsApplication: SensoricsApplication
    ) = BluetoothScanningViewModelFactory(
        bluetooth,
        sensoricsApplication
    )

    @Provides
    @BubbleActivityScope
    fun providePermissionsViewModel(bubbleActivity: BubbleActivity): PermissionViewModel {
        val permissionAction = PermissionActionFactory.getPermissionAction(bubbleActivity)
        return PermissionViewModel(permissionAction, bubbleActivity)
    }

    @Provides
    @BubbleActivityScope
    fun provideBluetoothViewModelFactory(
        bubbleActivity: BubbleActivity,
        bluetooth: Bluetooth,
        bluetoothStateReceiver: BluetoothStateReceiver
    ) = BluetoothViewModelFactory(bluetooth, bluetoothStateReceiver, bubbleActivity.application)

    @Provides
    @BubbleActivityScope
    fun provideBluetoothViewModel(
        bubbleActivity: BubbleActivity, bluetoothViewModelFactory: BluetoothViewModelFactory
    ) = ViewModelProviders.of(
        bubbleActivity,
        bluetoothViewModelFactory
    ).get(BluetoothViewModel::class.java)

    @Provides
    @BubbleActivityScope
    fun provideGetAllDevicesUseCase(
        deviceRepository: DeviceRepository
    ): GetSavedDevicesUseCase {
        return GetSavedDevicesUseCase(deviceRepository)
    }

    @Provides
    @BubbleActivityScope
    fun provideSaveDeviceUseCase(
        deviceRepository: DeviceRepository
    ): SaveDeviceUseCase {
        return SaveDeviceUseCase(deviceRepository)
    }

    @Provides
    @BubbleActivityScope
    fun provideDeleteDeviceUseCase(
        deviceRepository: DeviceRepository
    ): DeleteDeviceUseCase {
        return DeleteDeviceUseCase(deviceRepository)
    }

    @Provides
    @BubbleActivityScope
    fun provideUseCasesViewModel(
        bubbleActivity: BubbleActivity,
        useCasesViewModelFactory: UseCasesViewModelFactory
    ) = ViewModelProviders.of(bubbleActivity, useCasesViewModelFactory)
        .get(UseCasesViewModel::class.java)

    @Provides
    @BubbleActivityScope
    fun provideDeviceListViewModel(
        bubbleActivity: BubbleActivity,
        deviceListViewModelFactory: DeviceListViewModelFactory
    ): DeviceViewModel {
        return ViewModelProviders.of(bubbleActivity, deviceListViewModelFactory)
            .get(DeviceViewModel::class.java)
    }

    @Provides
    @BubbleActivityScope
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
    @BubbleActivityScope
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
    @BubbleActivityScope
    fun provideDashboardViewModel(
        bubbleActivity: BubbleActivity,
        useCasesViewModelFactory: DashboardViewModelFactory
    ) = ViewModelProviders.of(bubbleActivity, useCasesViewModelFactory)
        .get(DashboardViewModel::class.java)

    @Provides
    @BubbleActivityScope
    fun provideDashboardViewModelFactory(
        readingsStream: Flowable<List<Reading>>
    ) = DashboardViewModelFactory(
        readingsStream
    )

    @Provides
    @BubbleActivityScope
    fun provideLiveGraphViewModelFactory(
        getReadingsUseCase: GetReadingsUseCase,
        bubbleActivity: BubbleActivity
    ) = LiveGraphViewModelFactory(
        getReadingsUseCase,
        bubbleActivity.application
    )

    @Provides
    @BubbleActivityScope
    fun provideLiveGraphViewModel(
        liveGraphViewModelFactory: LiveGraphViewModelFactory,
        bubbleActivity: BubbleActivity
    ) = ViewModelProviders.of(
        bubbleActivity,
        liveGraphViewModelFactory
    ).get(LiveGraphViewModel::class.java)

    @Provides
    @BubbleActivityScope
    fun provideGetSensorReadingsUseCase(
        inMemoryRepository: InMemoryRepository
    ) = GetReadingsUseCase(inMemoryRepository)

    @Provides
    @BubbleActivityScope
    fun provideMainResourceViewModelFactory(
        getMainResourceUseCase: GetMainResourceUseCase
    ) = MainResourceViewModelFactory(getMainResourceUseCase)

    @Provides
    @BubbleActivityScope
    fun provideMainResourceViewModel(
        bubbleActivity: BubbleActivity,
        mainResourceViewModelFactory: MainResourceViewModelFactory
    ) = ViewModelProviders.of(bubbleActivity, mainResourceViewModelFactory)
        .get(MainResourceViewModel::class.java)
}