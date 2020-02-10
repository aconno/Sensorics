package com.aconno.sensorics.dagger.settings_framework

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.ui.settings_framework.BeaconSettingsActivity
import com.aconno.sensorics.viewmodel.BeaconSettingsTransporterSharedViewModel
import dagger.Module
import dagger.Provides

@Module
class BeaconSettingsActivityModule {

    @Provides
    @BeaconSettingsActivityScope
    fun provideBeaconSettingsSharedViewModel(
        beaconSettingsActivity: BeaconSettingsActivity,
        beaconSettingsViewModelFactory: BeaconSettingsSharedViewModelFactory
    ) = ViewModelProviders.of(beaconSettingsActivity, beaconSettingsViewModelFactory)
        .get(BeaconSettingsTransporterSharedViewModel::class.java)

    @Provides
    @BeaconSettingsActivityScope
    fun provideBeaconSettingsSharedViewModelFactory(
    ) = BeaconSettingsSharedViewModelFactory()


}