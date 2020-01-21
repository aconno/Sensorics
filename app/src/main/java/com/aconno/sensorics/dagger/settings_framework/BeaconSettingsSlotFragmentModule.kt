package com.aconno.sensorics.dagger.settings_framework

import com.aconno.sensorics.dagger.application.FragmentScope
import com.aconno.sensorics.ui.settings_framework.fragments.BeaconSettingsSlotFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BeaconSettingsSlotFragmentModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBeaconSettingsSlotFragment(): BeaconSettingsSlotFragment
}