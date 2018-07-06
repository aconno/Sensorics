package com.aconno.sensorics.ui.actions

interface ConditionDialogListener {

    fun onSetClicked(readingType: String, constraint: String, value: String)
}