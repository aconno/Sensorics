package com.aconno.acnsensa.dagger.livegraph

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.LiveGraphActivity
import dagger.Component

/**
 * @author aconno
 */
@Component(dependencies = [AppComponent::class], modules = [LiveGraphModule::class])
@LiveGraphScope
interface LiveGraphComponent {
    fun inject(liveGraphActivity: LiveGraphActivity)
}