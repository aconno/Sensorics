package com.aconno.acnsensa.ui.dialogs

import com.aconno.acnsensa.domain.model.Device

interface DevicesDialogListener {

    fun onDevicesDialogItemClick(item: Device)
}