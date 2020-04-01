package com.aconno.sensorics.dagger.publishlist

import com.aconno.sensorics.dagger.application.FragmentScope
import com.aconno.sensorics.ui.settings.publishers.PublishListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PublishListFragmentsModule {
    @ContributesAndroidInjector
    @FragmentScope
    abstract fun bindPublishListFragment(): PublishListFragment
}
