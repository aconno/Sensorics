package com.aconno.acnsensa.domain.ifttt.outcome

import com.aconno.acnsensa.domain.SmsSender
import com.aconno.acnsensa.domain.Vibrator
import com.aconno.acnsensa.domain.ifttt.NotificationDisplay
import com.aconno.acnsensa.domain.ifttt.TextToSpeechPlayer
import com.aconno.acnsensa.domain.interactor.type.CompletableUseCaseWithParameter
import io.reactivex.Completable

class RunOutcomeUseCase(
    private val outcomeExecutorSelector: OutcomeExecutorSelector
) : CompletableUseCaseWithParameter<Outcome> {
    override fun execute(parameter: Outcome): Completable {
        return Completable.fromAction { runOutcome(parameter) }
    }

    private fun runOutcome(outcome: Outcome) {
        val outcomeExecutor = outcomeExecutorSelector.selectOutcomeExecutor(outcome)
        outcomeExecutor.execute(outcome)
    }


}

class OutcomeExecutorSelector(
    private val notificationOutcomeExecutor: NotificationOutcomeExecutor,
    private val smsOutcomeExecutor: SmsOutcomeExecutor,
    private val textToSpeechOutcomeExecutor: TextToSpeechOutcomeExecutor,
    private val vibrationOutcomeExecutor: VibrationOutcomeExecutor
) {
    fun selectOutcomeExecutor(outcome: Outcome): OutcomeExecutor {
        return when (outcome.type) {
            Outcome.OUTCOME_TYPE_NOTIFICATION -> notificationOutcomeExecutor
            Outcome.OUTCOME_TYPE_SMS -> smsOutcomeExecutor
            Outcome.OUTCOME_TYPE_TEXT_TO_SPEECH -> textToSpeechOutcomeExecutor
            Outcome.OUTCOME_TYPE_VIBRATION -> vibrationOutcomeExecutor
            else -> throw IllegalArgumentException("Invalid Outcome type.")
        }
    }
}

class Outcome(val parameters: Map<String, String>, val type: Int) {

    companion object {
        const val OUTCOME_TYPE_NOTIFICATION = 0
        const val OUTCOME_TYPE_SMS = 1
        const val OUTCOME_TYPE_TEXT_TO_SPEECH = 2
        const val OUTCOME_TYPE_VIBRATION = 3

        const val TEXT_MESSAGE = "textMessage"
        const val PHONE_NUMBER = "phoneNumber"
    }
}

interface OutcomeExecutor {
    fun execute(outcome: Outcome)
}

class NotificationOutcomeExecutor(private val notificationDisplay: NotificationDisplay) :
    OutcomeExecutor {
    override fun execute(outcome: Outcome) {
        val message = outcome.parameters[Outcome.TEXT_MESSAGE]
        message?.let { notificationDisplay.displayAlertNotification(it) }
    }
}

class SmsOutcomeExecutor(private val smsSender: SmsSender) : OutcomeExecutor {
    override fun execute(outcome: Outcome) {
        if (!running) {
            running = true
            val startTime = System.currentTimeMillis()
            val message = outcome.parameters[Outcome.TEXT_MESSAGE]
            val phoneNumber = outcome.parameters[Outcome.PHONE_NUMBER]
            if (phoneNumber != null && message != null) {
                smsSender.sendSms(phoneNumber, message)
                while (System.currentTimeMillis() - startTime < OUTCOME_EXECUTION_TIME_MS) {
                    Thread.sleep(100)
                }
            }

            running = false
        }
    }

    companion object {
        private var running = false
        private const val OUTCOME_EXECUTION_TIME_MS = 8_000
    }
}

class TextToSpeechOutcomeExecutor(
    private val textToSpeechPlayer: TextToSpeechPlayer
) : OutcomeExecutor {
    override fun execute(outcome: Outcome) {
        val message = outcome.parameters[Outcome.TEXT_MESSAGE]
        message?.let {
            textToSpeechPlayer.play(message)
        }
    }
}

class VibrationOutcomeExecutor(private val vibrator: Vibrator) : OutcomeExecutor {
    override fun execute(outcome: Outcome) {
        if (!running) {
            running = true
            val startTime = System.currentTimeMillis()
            vibrator.vibrate(4000)
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