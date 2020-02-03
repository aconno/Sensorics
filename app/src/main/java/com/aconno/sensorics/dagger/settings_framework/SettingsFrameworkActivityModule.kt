package com.aconno.sensorics.dagger.settings_framework

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.ui.settings_framework.SettingsFrameworkActivity
import com.aconno.sensorics.ui.settings_framework.fragments.SettingsActivitySharedViewModel
import dagger.Module
import dagger.Provides

@Module
class SettingsFrameworkActivityModule {

    @Provides
    @SettingsFrameworkActivityScope
    fun provideSettingsActivitySharedViewModel(
        settingsFrameworkActivity: SettingsFrameworkActivity,
        beaconSettingsViewModelFactory: SettingsActivitySharedViewModelFactory
    ) = ViewModelProviders.of(settingsFrameworkActivity, beaconSettingsViewModelFactory)
        .get(SettingsActivitySharedViewModel::class.java)

    @Provides
    @SettingsFrameworkActivityScope
    fun provideSettingsActivitySharedViewModelFactory(
    ) = SettingsActivitySharedViewModelFactory()


}