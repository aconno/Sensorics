package com.aconno.sensorics.dagger.livegraph

import com.aconno.sensorics.dagger.application.AppComponent
import com.aconno.sensorics.ui.LiveGraphActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [LiveGraphModule::class])
@LiveGraphScope
interface LiveGraphComponent {
    fun inject(liveGraphActivity: LiveGraphActivity)
}