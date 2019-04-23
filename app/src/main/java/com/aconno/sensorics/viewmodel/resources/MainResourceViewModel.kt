package com.aconno.sensorics.viewmodel.resources

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.resources.GetMainResourceUseCase
import io.reactivex.Single

class MainResourceViewModel(
    private val getMainResourceUseCase: GetMainResourceUseCase
) : ViewModel() {

    fun getResourcePath(deviceName: String): Single<String> {
        return getMainResourceUseCase.execute(deviceName)
    }

}