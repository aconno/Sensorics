package com.aconno.sensorics.dagger.connect

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.resources.GetConnectionResourceUseCase
import com.aconno.sensorics.domain.interactor.resources.GetMainResourceUseCase
import com.aconno.sensorics.ui.connect.ConnectActivity
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModel
import com.aconno.sensorics.viewmodel.resources.MainResourceViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ConnectActivityModule {
    @Provides
    @ConnectActivityScope
    fun provideMainResourceViewModelFactory(
        getMainResourceUseCase: GetMainResourceUseCase,
        getConnectionResourceUseCase: GetConnectionResourceUseCase
    ) = MainResourceViewModelFactory(getMainResourceUseCase, getConnectionResourceUseCase)

    @Provides
    @ConnectActivityScope
    fun provideMainResourceViewModel(
        connectActivity: ConnectActivity,
        mainResourceViewModelFactory: MainResourceViewModelFactory
    ) = ViewModelProviders.of(connectActivity, mainResourceViewModelFactory)
        .get(MainResourceViewModel::class.java)
}