package com.aconno.sensorics.ui.dialogs

import com.aconno.sensorics.domain.model.Device

interface ScannedDevicesDialogListener {

    fun onDevicesDialogItemClick(item: Device)
}