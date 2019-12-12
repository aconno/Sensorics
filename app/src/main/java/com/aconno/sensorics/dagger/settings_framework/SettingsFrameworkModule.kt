package com.aconno.sensorics.dagger.settings_framework

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.ui.settings_framework.BeaconSettingsViewModel
import com.aconno.sensorics.ui.settings_framework.BeaconSettingsViewModelFactory
import com.aconno.sensorics.ui.settings_framework.SettingsFrameworkActivity
import dagger.Module
import dagger.Provides

@Module
class SettingsFrameworkModule {

    @Provides
    @SettingsFrameworkScope
    fun provideBeaconSettingsViewModel(
        settingsFrameworkActivity: SettingsFrameworkActivity,
        beaconSettingsViewModelFactory: BeaconSettingsViewModelFactory
    ) = ViewModelProviders.of(settingsFrameworkActivity, beaconSettingsViewModelFactory)
        .get(BeaconSettingsViewModel::class.java)

    @Provides
    @SettingsFrameworkScope
    fun provideBeaconSettingsViewModelFactory(
    ) = BeaconSettingsViewModelFactory()


}