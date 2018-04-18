package com.aconno.acnsensa.ui.actions

import com.aconno.acnsensa.domain.model.SensorTypeSingle

interface ConditionDialogListener {

    fun onSetClicked(sensorType: SensorTypeSingle, condition: String, value: String)
}