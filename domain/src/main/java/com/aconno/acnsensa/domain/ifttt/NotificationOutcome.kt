package com.aconno.acnsensa.domain.ifttt

/**
 * @author aconno
 */
class NotificationOutcome(
    val message: String,
    private val notificationDisplay: NotificationDisplay
) : Outcome {
    override fun execute() {
        notificationDisplay.displayAlertNotification(message)
    }
}