package com.aconno.sensorics.dagger.virtualscanningsourcelist

import com.aconno.sensorics.dagger.application.FragmentScope
import com.aconno.sensorics.ui.settings.virtualscanningsources.VirtualScanningSourceListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VirtualScanningSourceListActivityFragmentsModule {
    @ContributesAndroidInjector
    @FragmentScope
    abstract fun bindVirtualScanningSourceListFragment(): VirtualScanningSourceListFragment
}