package com.aconno.sensorics.dagger.connect

import androidx.lifecycle.ViewModelProviders
import com.aconno.sensorics.domain.interactor.resources.GetConnectionResourceUseCase
import com.aconno.sensorics.ui.connect.ConnectActivity
import com.aconno.sensorics.viewmodel.connection.ConnectionViewModel
import com.aconno.sensorics.viewmodel.connection.ConnectionViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class ConnectActivityModule {
    @Provides
    @ConnectActivityScope
    fun provideConnectionViewModelFactory(
        getConnectionResourceUseCase: GetConnectionResourceUseCase
    ) = ConnectionViewModelFactory(getConnectionResourceUseCase)

    @Provides
    @ConnectActivityScope
    fun provideConnectionViewModel(
        connectActivity: ConnectActivity,
        connectionViewModelFactory: ConnectionViewModelFactory
    ) = ViewModelProviders.of(connectActivity, connectionViewModelFactory)
        .get(ConnectionViewModel::class.java)
}