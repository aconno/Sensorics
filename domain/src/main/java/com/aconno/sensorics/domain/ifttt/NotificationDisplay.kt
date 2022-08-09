package com.aconno.sensorics.domain.ifttt

/**
 * @author aconno
 */
interface NotificationDisplay {
    fun displayAlertNotification(message: String, notificationId : Int?)
}