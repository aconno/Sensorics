package com.aconno.acnsensa.dagger

import android.bluetooth.BluetoothAdapter
import com.aconno.acnsensa.AcnSensaApplication
import com.aconno.acnsensa.device.bluetooth.BluetoothImpl
import com.aconno.acnsensa.device.bluetooth.BluetoothPermission
import com.aconno.acnsensa.device.bluetooth.BluetoothPermissionImpl
import com.aconno.acnsensa.domain.Bluetooth
import dagger.Module
import dagger.Provides
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
}