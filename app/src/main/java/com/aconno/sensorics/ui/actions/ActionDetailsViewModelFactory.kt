package com.aconno.sensorics.ui.actions

import android.app.Application
import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionByIdUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory
import io.reactivex.Flowable

class ActionDetailsViewModelFactory(
    private val application: Application,
    private val savedDevicesStream: Flowable<List<Device>>,
    private val formatMatcher: FormatMatcher,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val addActionUseCase: AddActionUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ActionDetailsViewModel(
            application,
            savedDevicesStream,
            formatMatcher,
            getActionByIdUseCase,
            addActionUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}