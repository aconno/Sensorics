package com.aconno.sensorics.ui.actions

import androidx.lifecycle.ViewModel
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.interactor.ifttt.action.GetActionByIdUseCase
import com.aconno.sensorics.domain.interactor.resources.GetIconUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory
import io.reactivex.Flowable

class ActionDetailsViewModelFactory(
    private val savedDevicesStream: Flowable<List<Device>>,
    private val formatMatcher: FormatMatcher,
    private val getActionByIdUseCase: GetActionByIdUseCase,
    private val addActionUseCase: AddActionUseCase,
    private val getIconUseCase: GetIconUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = ActionDetailsViewModel(
            savedDevicesStream,
            formatMatcher,
            getActionByIdUseCase,
            addActionUseCase,
            getIconUseCase
        )
        return getViewModel(viewModel, modelClass)
    }
}