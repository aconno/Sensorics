package com.aconno.sensorics.ui.actions

import android.arch.lifecycle.ViewModel
import com.aconno.sensorics.domain.format.FormatMatcher
import com.aconno.sensorics.domain.interactor.ifttt.action.AddActionUseCase
import com.aconno.sensorics.domain.model.Device
import com.aconno.sensorics.viewmodel.factory.BaseViewModelFactory
import io.reactivex.Flowable

class ActionDetailsViewModelFactory(
    private val savedDevicesStream: Flowable<List<Device>>,
    private val formatMatcher: FormatMatcher,
    private val addActionUseCase: AddActionUseCase
) : BaseViewModelFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val viewModel = ActionDetailsViewModel(savedDevicesStream, formatMatcher, addActionUseCase)
        return getViewModel(viewModel, modelClass)
    }
}