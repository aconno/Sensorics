package com.aconno.acnsensa.dagger.mainactivity

import android.arch.lifecycle.ViewModelProviders
import android.support.v4.content.LocalBroadcastManager
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.BluetoothStateReceiver
import com.aconno.acnsensa.device.permissons.PermissionActionFactory
import com.aconno.acnsensa.domain.Bluetooth
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.viewmodel.BluetoothScanningViewModel
import com.aconno.acnsensa.viewmodel.BluetoothViewModel
import com.aconno.acnsensa.viewmodel.PermissionViewModel
import com.aconno.acnsensa.viewmodel.SensorListViewModel
import com.aconno.acnsensa.viewmodel.factory.BluetoothScanningViewModelFactory
import com.aconno.acnsensa.viewmodel.factory.BluetoothViewModelFactory
import com.aconno.acnsensa.viewmodel.factory.SensorListViewModelFactory
import dagger.Module
import dagger.Provides
import io.reactivex.Flowable

/**
 * @author aconno
 */
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
        sensorValues: Flowable<Map<String, Number>>
    ) = SensorListViewModelFactory(sensorValues)

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
        bluetoothStateReceiver: BluetoothStateReceiver,
        localBroadcastManager: LocalBroadcastManager
    ) =
        BluetoothViewModelFactory(bluetooth, bluetoothStateReceiver, localBroadcastManager)

    @Provides
    @MainActivityScope
    fun provideBluetoothViewModel(bluetoothViewModelFactory: BluetoothViewModelFactory) =
        ViewModelProviders.of(
            mainActivity,
            bluetoothViewModelFactory
        ).get(BluetoothViewModel::class.java)
}