package com.aconno.acnsensa.ui.dialogs

import com.aconno.acnsensa.domain.model.Device

interface ScannedDevicesDialogListener {

    fun onDevicesDialogItemClick(item: Device)
}