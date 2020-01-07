package com.aconno.sensorics.dagger.configure

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.ui.configure.BeaconViewModel
import com.aconno.sensorics.ui.configure.BeaconViewModelFactory
import com.aconno.sensorics.ui.configure.ConfigureActivity
import dagger.Module
import dagger.Provides

@Module
class ConfigureActivityModule {
    @Provides
    @ConfigureActivityScope
    fun provideBeaconViewModel(
        configureActivity: ConfigureActivity,
        beaconViewModelFactory: BeaconViewModelFactory
    ) = ViewModelProviders.of(configureActivity, beaconViewModelFactory)
        .get(BeaconViewModel::class.java)

    @Provides
    @ConfigureActivityScope
    fun provideBeaconViewModelFactory(
    ) = BeaconViewModelFactory()

}