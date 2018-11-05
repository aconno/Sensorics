package com.aconno.sensorics.dagger.mainactivity

import com.aconno.sensorics.dagger.application.FragmentScope
import com.aconno.sensorics.ui.UseCasesFragment
import com.aconno.sensorics.ui.acnact.AcnActFragment
import com.aconno.sensorics.ui.acnrange.AcnRangeFragment
import com.aconno.sensorics.ui.dashboard.DashboardFragment
import com.aconno.sensorics.ui.devices.SavedDevicesFragment
import com.aconno.sensorics.ui.dialogs.ScannedDevicesDialog
import com.aconno.sensorics.ui.readings.GenericReadingListFragment
import com.aconno.sensorics.ui.sensors.SensorListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityFragmentsModule {

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindUseCasesFragment(): UseCasesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSavedDevicesFragment(): SavedDevicesFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindSensorListFragment(): SensorListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindScannedDevicesDialog(): ScannedDevicesDialog

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindAcnRangeFragment(): AcnRangeFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindDashboardFragment(): DashboardFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindGenericReadingListFragment(): GenericReadingListFragment

    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindAcnActFragment(): AcnActFragment

}
