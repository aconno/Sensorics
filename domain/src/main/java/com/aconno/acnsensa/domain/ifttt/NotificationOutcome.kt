package com.aconno.acnsensa.domain.ifttt

/**
 * @author aconno
 */
class NotificationOutcome(
    private val message: String,
    private val notificationDisplay: NotificationDisplay
) : Outcome {
    override fun execute() {
        notificationDisplay.displayAlertNotification(message)
    }
}