package com.aconno.sensorics.dagger.actionlist

import com.aconno.sensorics.dagger.application.FragmentScope
import com.aconno.sensorics.ui.ActionListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActionListFragmentsModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun bindActionListFragment(): ActionListFragment
}
