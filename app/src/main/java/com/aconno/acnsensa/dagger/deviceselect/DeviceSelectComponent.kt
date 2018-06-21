package com.aconno.acnsensa.dagger.deviceselect

import com.aconno.acnsensa.dagger.application.AppComponent
import com.aconno.acnsensa.ui.settings.publishers.DeviceSelectFragment
import dagger.Component

@Component(dependencies = [AppComponent::class], modules = [DeviceSelectModule::class])
@DeviceSelectScope
interface DeviceSelectComponent {
    //Exposed dependencies for child components.

    //Classes which can accept injected dependencies.
    fun inject(deviceSelectFragment: DeviceSelectFragment)
}