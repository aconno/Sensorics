package com.aconno.acnsensa.ui.dialogs

import com.aconno.acnsensa.domain.model.Device

interface SavedDevicesDialogListener {

    fun onSavedDevicesDialogItemClick(item: Device)
}