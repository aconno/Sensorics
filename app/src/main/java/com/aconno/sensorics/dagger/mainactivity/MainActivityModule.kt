package com.aconno.sensorics.dagger.mainactivity

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.BluetoothStateReceiver
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.permissons.PermissionActionFactory
import com.aconno.sensorics.device.usecase.LocalUseCaseRepositoryImpl
import com.aconno.sensorics.device.usecase.RemoteUseCaseRepositoryImpl
import com.aconno.sensorics.device.usecase.RetrofitUseCaseApi
import com.aconno.sensorics.domain.interactor.filter.FilterByMacUseCase
import com.aconno.sensorics.domain.interactor.repository.DeleteDeviceUseCase
import com.aconno.sensorics.domain.interactor.repository.GetSavedDevicesUseCase
import com.aconno.sensorics.domain.interactor.repository.SaveDeviceUseCase
import com.aconno.sensorics.domain.model.Reading
import com.aconno.sensorics.domain.model.ScanDevice
import com.aconno.sensorics.domain.repository.DeviceRepository
import com.aconno.sensorics.domain.repository.LocalUseCaseRepository
import com.aconno.sensorics.domain.repository.RemoteUseCaseRepository
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.readings.ReadingListViewModel
import com.aconno.sensorics.ui.readings.ReadingListViewModelFactory
import com.aconno.sensorics.viewmodel.*
import com.aconno.sensorics.viewmodel.factory.*
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory


@Module
class MainActivityModule {

    @Provides
    @MainActivityScope
    fun provideSensorListViewModel(
        mainActivity: MainActivity,
        sensorListViewModelFactory: SensorListViewModelFactory
    ) =
        ViewModelProviders.of(mainActivity, sensorListViewModelFactory)
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
    ) =
        ViewModelProviders.of(mainActivity, readingListViewModelFactory)
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
    fun provideBluetoothScanningViewModelFactory(
        bluetooth: Bluetooth,
        sensoricsApplication: SensoricsApplication
    ) = BluetoothScanningViewModelFactory(
        bluetooth,
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
    ) =
        BluetoothViewModelFactory(bluetooth, bluetoothStateReceiver, mainActivity.application)

    @Provides
    @MainActivityScope
    fun provideBluetoothViewModel(
        mainActivity: MainActivity, bluetoothViewModelFactory: BluetoothViewModelFactory
    ) =
        ViewModelProviders.of(
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
        mainActivity: MainActivity,
        useCasesViewModelFactory: UseCasesViewModelFactory
    ) =
        ViewModelProviders.of(mainActivity, useCasesViewModelFactory)
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
        deleteDeviceUseCase: DeleteDeviceUseCase
    ): DeviceListViewModelFactory {
        val deviceStream = scanDeviceStream.map { it.device }
        return DeviceListViewModelFactory(
            deviceStream,
            getSavedDevicesUseCase,
            saveDeviceUseCase,
            deleteDeviceUseCase
        )
    }

    @Provides
    @MainActivityScope
    fun provideUseCasesViewModelFactory(
        readingsStream: Flowable<List<Reading>>,
        filterByMacUseCase: FilterByMacUseCase,
        remoteUseCaseRepository: RemoteUseCaseRepository
    ) = UseCasesViewModelFactory(
        readingsStream,
        filterByMacUseCase,
        remoteUseCaseRepository
    )

    @Provides
    @MainActivityScope
    fun provideRetrofitUseCaseApi(): RetrofitUseCaseApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://playground.simvelop.de:8095")
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        return retrofit.create(RetrofitUseCaseApi::class.java)
    }

    @Provides
    @MainActivityScope
    fun provideRemoteUseCaseRepository(
        retrofitUseCaseApi: RetrofitUseCaseApi,
        localUseCaseRepository: LocalUseCaseRepository
    ): RemoteUseCaseRepository =
        RemoteUseCaseRepositoryImpl(retrofitUseCaseApi, localUseCaseRepository)

    @Provides
    @MainActivityScope
    fun provideUseCaseRepository(
        context: SensoricsApplication
    ): LocalUseCaseRepository =
        LocalUseCaseRepositoryImpl(context)

    @Provides
    @MainActivityScope
    fun provideDashboardViewModel(
        mainActivity: MainActivity,
        useCasesViewModelFactory: DashboardViewModelFactory
    ) =
        ViewModelProviders.of(mainActivity, useCasesViewModelFactory)
            .get(DashboardViewModel::class.java)

    @Provides
    @MainActivityScope
    fun provideDashboardViewModelFactory(
        readingsStream: Flowable<List<Reading>>
    ) = DashboardViewModelFactory(
        readingsStream
    )
}
