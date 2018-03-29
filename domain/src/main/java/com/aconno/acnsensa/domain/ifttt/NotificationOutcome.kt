package com.aconno.acnsensa.domain.ifttt

/**
 * @author aconno
 */
class NotificationOutcome(
    val message: String,
    private val notificationDisplay: NotificationDisplay
) : Outcome {

    //TODO: Remove logic

    val time = 10_000
    override fun execute() {
        if (!running) {
            running = true
            val now = System.currentTimeMillis()
            notificationDisplay.displayAlertNotification(message)
            while (System.currentTimeMillis() - now < time) {
                Thread.sleep(100)
            }

            running = false
        }
    }

    companion object {
        var running = false
    }
}