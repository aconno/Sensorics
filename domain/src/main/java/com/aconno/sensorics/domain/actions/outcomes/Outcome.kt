package com.aconno.sensorics.domain.actions.outcomes

import com.aconno.sensorics.domain.actions.Action

class Outcome(val parameters: Map<String, String>, val type: Int) {
    var sourceAction : Action? = null

    override fun toString(): String {
        return when (type) {
            0 -> "Notification"
            1 -> "SMS"
            2 -> "Text to Speech"
            3 -> "Vibration"
            4 -> "Alarm"
            else -> throw IllegalArgumentException("Outcome type not valid: $type")
        }
    }

    companion object {

        const val OUTCOME_TYPE_NOTIFICATION = 0
        const val OUTCOME_TYPE_SMS = 1
        const val OUTCOME_TYPE_TEXT_TO_SPEECH = 2
        const val OUTCOME_TYPE_VIBRATION = 3
        const val OUTCOME_TYPE_ALARM = 4

        const val TEXT_MESSAGE = "textMessage"
        const val PHONE_NUMBER = "phoneNumber"

    }
}