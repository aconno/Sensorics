package com.aconno.sensorics.viewmodel.connection

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.resources.GetConnectionResourceUseCase
import io.reactivex.Single

class ConnectionViewModel(
    private val getConnectionResourceUseCase: GetConnectionResourceUseCase
) : ViewModel() {

    fun getConnectionResourcePath(deviceName: String): Single<String> {
        return getConnectionResourceUseCase.execute(deviceName)
    }
}