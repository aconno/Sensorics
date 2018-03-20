package com.aconno.acnsensa.dagger

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