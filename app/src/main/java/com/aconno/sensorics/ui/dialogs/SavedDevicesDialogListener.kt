package com.aconno.sensorics.ui.dialogs

import com.aconno.sensorics.domain.model.Device

interface SavedDevicesDialogListener {

    fun onSavedDevicesDialogItemClick(item: Device)
}