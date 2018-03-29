package com.aconno.acnsensa.domain.ifttt

import com.aconno.acnsensa.domain.SmsSender

class SmsOutcome(
    private val smsSender: SmsSender,
    val phoneNumber: String,
    private val message: String
) : Outcome {
    override fun execute() {
        if (!running) {
            running = true
            val startTime = System.currentTimeMillis()
            smsSender.sendSms(phoneNumber, message)
            while (System.currentTimeMillis() - startTime < OUTCOME_EXECUTION_TIME_MS) {
                Thread.sleep(100)
            }

            running = false
        }
    }

    companion object {
        var running = false
        private const val OUTCOME_EXECUTION_TIME_MS = 8_000
    }
}