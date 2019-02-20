package com.aconno.sensorics.dagger.logging

import android.arch.lifecycle.ViewModelProviders
import com.aconno.sensorics.ui.configure.BeaconViewModel
import com.aconno.sensorics.ui.configure.BeaconViewModelFactory
import com.aconno.sensorics.ui.logs.LoggingActivity
import dagger.Module
import dagger.Provides

@Module
class LoggingModule {
    @Provides
    @LoggingScope
    fun provideBeaconViewModel(
            loggingActivity: LoggingActivity,
            beaconViewModelFactory: BeaconViewModelFactory
    ) = ViewModelProviders.of(loggingActivity, beaconViewModelFactory)
            .get(BeaconViewModel::class.java)

    @Provides
    @LoggingScope
    fun provideBeaconViewModelFactory(
    ) = BeaconViewModelFactory()
}