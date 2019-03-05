package com.aconno.sensorics.viewmodel.factory

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.interactor.logs.AddLogUseCase
import com.aconno.sensorics.domain.interactor.logs.DeleteDeviceLogsUseCase
import com.aconno.sensorics.domain.interactor.logs.GetDeviceLogsUseCase
import com.aconno.sensorics.model.mapper.LogModelMapper
import com.aconno.sensorics.viewmodel.LoggingViewModel

class LoggingViewModelFactory(private val getDeviceLogsUseCase: GetDeviceLogsUseCase,
                              private val deleteDeviceLogsUseCase: DeleteDeviceLogsUseCase,
                              private val addLogUseCase: AddLogUseCase,
                              private val logModelMapper: LogModelMapper): BaseViewModelFactory() {


    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = LoggingViewModel(getDeviceLogsUseCase,
                deleteDeviceLogsUseCase, addLogUseCase, logModelMapper)
        return getViewModel(viewModel, modelClass)
    }
}