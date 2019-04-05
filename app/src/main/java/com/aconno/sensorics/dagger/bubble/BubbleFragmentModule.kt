package com.aconno.sensorics.dagger.bubble

import com.aconno.sensorics.dagger.application.FragmentScope
import com.aconno.sensorics.ui.device_main.DeviceMainFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BubbleFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun deviceMainFragment(): DeviceMainFragment
}
