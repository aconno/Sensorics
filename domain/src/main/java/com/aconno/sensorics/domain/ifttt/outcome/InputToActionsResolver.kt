package com.aconno.sensorics.domain.ifttt.outcome

import com.aconno.sensorics.domain.actions.Action

interface InputToActionsResolver {
    fun getActionsForInputParameters(inputDeviceMacAddress : String, inputType : String) : List<Action>
}