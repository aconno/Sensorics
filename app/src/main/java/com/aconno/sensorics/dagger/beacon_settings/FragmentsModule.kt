package com.aconno.sensorics.dagger.beacon_settings

import com.aconno.sensorics.dagger.application.FragmentScope
import com.aconno.sensorics.ui.beacon_settings.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentsModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBeaconSettingsSlotFragment(): BeaconSettingsSlotFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBeaconSettingsGeneralFragment(): BeaconSettingsGeneralFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBeaconSettingsArbitraryDataHtmlFragment(): BeaconSettingsArbitraryDataFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBeaconSettingsCacheableParamsFragment(): BeaconSettingsCacheableParamsFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindBeaconSettingsParametersFragment(): BeaconSettingsParametersFragment


}