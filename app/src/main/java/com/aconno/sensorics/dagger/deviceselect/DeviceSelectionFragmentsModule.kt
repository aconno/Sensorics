package com.aconno.sensorics.dagger.deviceselect

import com.aconno.sensorics.ui.settings.publishers.DeviceSelectFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class DeviceSelectionFragmentsModule {
    @DeviceSelectScope
    @ContributesAndroidInjector(modules = [DeviceSelectModule::class])
    abstract fun bindDeviceSelectFragment(): DeviceSelectFragment
}
