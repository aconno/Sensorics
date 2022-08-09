package com.aconno.sensorics.ui.actions

import com.aconno.sensorics.domain.ifttt.LimitCondition

interface LimitConditionDialogListener {
    fun applyLimitCondition(limitCondition: LimitCondition)
}