package com.aconno.sensorics.dagger.mainactivity

import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.ui.MainActivity
import com.aconno.sensorics.ui.acnrange.AcnRangeFragment
import com.aconno.sensorics.ui.devices.SavedDevicesFragment
import com.aconno.sensorics.ui.readings.GenericReadingListFragment
import com.aconno.sensorics.ui.sensors.SensorListFragment
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [MainActivityModule::class])
@MainActivityScope
interface MainActivityComponent {

    fun inject(mainActivity: MainActivity)

    fun inject(sensorListFragment: SensorListFragment)

    fun inject(savedDevicesFragment: SavedDevicesFragment)

    fun inject(genericReadingListFragment: GenericReadingListFragment)

    fun inject(acnRangeFragment: AcnRangeFragment)
}