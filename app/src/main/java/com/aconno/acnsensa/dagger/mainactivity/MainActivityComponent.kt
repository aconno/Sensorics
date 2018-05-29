package com.aconno.acnsensa.dagger.mainactivity

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.MainActivity
import com.aconno.acnsensa.ui.SensorListFragment
import com.aconno.acnsensa.ui.beacons.BeaconListFragment
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [MainActivityModule::class])
@MainActivityScope
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(sensorListFragment: SensorListFragment)
    fun inject(beaconListFragment: BeaconListFragment)
}