package com.aconno.acnsensa.dagger

import com.aconno.acnsensa.sensorlist.MainActivity
import com.aconno.acnsensa.sensorlist.SensorListFragment
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [MainActivityModule::class])
@MainActivityScope
interface MainActivityComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(sensorListFragment: SensorListFragment)
}