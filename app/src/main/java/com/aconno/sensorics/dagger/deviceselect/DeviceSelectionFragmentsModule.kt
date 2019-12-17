package com.aconno.sensorics.dagger.deviceselect

import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeviceSelectionFragmentsModule {
    @DeviceSelectActivityScope
    @ContributesAndroidInjector(modules = [DeviceSelectActivityModule::class])
    abstract fun bindDeviceSelectFragment(): DeviceSelectFragment
}
