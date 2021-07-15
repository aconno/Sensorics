package com.aconno.sensorics.dagger.beacon_settings

import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.device.beacon.Beacon
import com.aconno.sensorics.device.beacon.v2.BeaconImpl
import com.aconno.sensorics.device.bluetooth.BluetoothTaskProcessorImpl
import com.aconno.sensorics.domain.scanning.Bluetooth
import com.aconno.sensorics.domain.scanning.BluetoothTaskProcessor
import com.aconno.sensorics.model.mapper.WebViewAppBeaconMapper
import com.aconno.sensorics.ui.beacon_settings.BeaconSettingsActivity
import com.aconno.sensorics.viewmodel.BeaconSettingsTransporterSharedViewModel
import com.aconno.sensorics.viewmodel.BeaconSettingsViewModel
import com.aconno.sensorics.viewmodel.factory.BeaconSettingsViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class BeaconSettingsActivityModule {

    @Provides
    @BeaconSettingsActivityScope
    fun provideBeacon(taskProcessor: BluetoothTaskProcessor): Beacon = BeaconImpl(taskProcessor)

    @Provides
    @BeaconSettingsActivityScope
    fun provideTaskProcessor(bluetooth: Bluetooth): BluetoothTaskProcessor =
        BluetoothTaskProcessorImpl(bluetooth)

    @Provides
    @BeaconSettingsActivityScope
    fun provideBeaconSettingsTransporterSharedViewModel(
        beaconSettingsActivity: BeaconSettingsActivity,
        beaconSettingsViewModelFactory: BeaconSettingsSharedViewModelFactory
    ) = ViewModelProvider(beaconSettingsActivity, beaconSettingsViewModelFactory)
        .get(BeaconSettingsTransporterSharedViewModel::class.java)

    @Provides
    @BeaconSettingsActivityScope
    fun provideBeaconSettingsViewModel(
        beaconSettingsActivity: BeaconSettingsActivity,
        beaconSettingsViewModelFactory: BeaconSettingsViewModelFactory
    ) = ViewModelProvider(beaconSettingsActivity, beaconSettingsViewModelFactory)
        .get(BeaconSettingsViewModel::class.java)

    @Provides
    @BeaconSettingsActivityScope
    fun provideBeaconSettingsSharedViewModelFactory(
    ) = BeaconSettingsSharedViewModelFactory()


    @Provides
    @BeaconSettingsActivityScope
    fun provideBeaconSettingsViewModelFactory(
        bluetooth: Bluetooth,
        beacon: Beacon,
        webViewAppBeaconMapper: WebViewAppBeaconMapper
    ) = BeaconSettingsViewModelFactory(bluetooth, beacon, webViewAppBeaconMapper)


}