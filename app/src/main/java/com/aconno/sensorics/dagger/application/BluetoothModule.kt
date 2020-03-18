package com.aconno.sensorics.dagger.application

import android.bluetooth.BluetoothAdapter
import android.content.IntentFilter
import android.content.SharedPreferences
import com.aconno.sensorics.BluetoothStateReceiver
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.BluetoothCharacteristicValueConverter
import com.aconno.sensorics.device.bluetooth.BluetoothImpl
import com.aconno.sensorics.device.bluetooth.BluetoothPermission
import com.aconno.sensorics.device.bluetooth.BluetoothPermissionImpl
import com.aconno.sensorics.device.bluetooth.BluetoothStateListener
import com.aconno.sensorics.domain.scanning.Bluetooth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetooth(
        sensoricsApplication: SensoricsApplication,
        sharedPreferences: SharedPreferences,
        bluetoothAdapter: BluetoothAdapter,
        bluetoothPermission: BluetoothPermission,
        bluetoothCharacteristicValueConverter: BluetoothCharacteristicValueConverter
    ): Bluetooth {

        val listener = BluetoothStateListener()
        BluetoothStateReceiver(listener).also {
            sensoricsApplication.registerReceiver(
                it,
                IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            )
        }

        return BluetoothImpl(
            sensoricsApplication,
            sharedPreferences,
            bluetoothAdapter,
            bluetoothPermission,
            listener,
            bluetoothCharacteristicValueConverter
        )
    }

    @Provides
    @Singleton
    fun provideBluetoothAdapter(): BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @Provides
    @Singleton
    fun provideBluetoothPermission(): BluetoothPermission = BluetoothPermissionImpl()
}