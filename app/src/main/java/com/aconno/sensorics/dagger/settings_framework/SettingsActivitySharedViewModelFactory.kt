package com.aconno.sensorics.dagger.settings_framework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aconno.sensorics.viewmodel.SettingsActivitySharedViewModel
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class SettingsActivitySharedViewModelFactory @Inject constructor() : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingsActivitySharedViewModel::class.java)) {
            SettingsActivitySharedViewModel() as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}