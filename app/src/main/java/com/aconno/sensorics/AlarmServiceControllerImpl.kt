package com.aconno.sensorics

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.domain.AlarmServiceController
import com.aconno.sensorics.domain.AlarmServiceController.Companion.ACTION_ALARM_SNOOZE

class AlarmServiceControllerImpl(
    val context: Context,
    val broadcastManager: LocalBroadcastManager
) : AlarmServiceController {
    override fun start() {
        context.startService(Intent(context, AlarmService::class.java))
    }

    override fun stop() {
        broadcastManager.sendBroadcast(Intent(ACTION_ALARM_SNOOZE))
    }
}