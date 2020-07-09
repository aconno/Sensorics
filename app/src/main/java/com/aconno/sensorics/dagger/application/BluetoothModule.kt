package com.aconno.sensorics.dagger.application

import android.bluetooth.BluetoothAdapter
import com.aconno.sensorics.BluetoothStateReceiver
import com.aconno.sensorics.SensoricsApplication
import com.aconno.sensorics.device.BluetoothGattAttributeValueConverter
import com.aconno.sensorics.device.bluetooth.BluetoothImpl
import com.aconno.sensorics.device.bluetooth.BluetoothPermission
import com.aconno.sensorics.device.bluetooth.BluetoothPermissionImpl
import com.aconno.sensorics.device.bluetooth.BluetoothStateListener
import com.aconno.sensorics.device.settings.LocalSettings
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.troido.bless.BleScanner
import com.troido.bless.BleScannerImpl
import com.troido.bless.rxjava3.RxBleScanner
import com.troido.bless.rxjava3.RxBleScannerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class BluetoothModule {

    @Provides
    @Singleton
    fun provideBluetoothStateReceiver(bluetoothStateListener: BluetoothStateListener) =
        BluetoothStateReceiver(bluetoothStateListener)

    @Provides
    @Singleton
    fun provideBluetoothStateListener() = BluetoothStateListener()

    @Provides
    @Singleton
    fun provideBluetooth(
        sensoricsApplication: SensoricsApplication,
        localSettings: LocalSettings,
        bluetoothAdapter: BluetoothAdapter,
        bluetoothPermission: BluetoothPermission,
        bluetoothStateListener: BluetoothStateListener,
        bluetoothGattAttributeValueConverter: BluetoothGattAttributeValueConverter,
        rxBleScanner: RxBleScanner
    ): Bluetooth =
        BluetoothImpl(
            sensoricsApplication,
            localSettings,
            bluetoothAdapter,
            bluetoothPermission,
            bluetoothStateListener,
            bluetoothGattAttributeValueConverter,
            rxBleScanner
        )

    @Provides
    @Singleton
    fun provideRxBleScanner(bleScanner: BleScanner): RxBleScanner = RxBleScannerImpl(bleScanner)

    @Provides
    @Singleton
    fun provideBleScanner(
        bluetoothAdapter: BluetoothAdapter
    ): BleScanner = BleScannerImpl(bluetoothAdapter)

    @Provides
    @Singleton
    fun provideBluetoothAdapter(): BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    @Provides
    @Singleton
    fun provideBluetoothPermission(): BluetoothPermission = BluetoothPermissionImpl()
}