package com.aconno.sensorics.service

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.telephony.TelephonyManager
import androidx.annotation.Nullable
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.aconno.sensorics.CpuWakeLock
import com.aconno.sensorics.domain.AlarmServiceController.Companion.ACTION_ALARM_SNOOZE
import com.aconno.sensorics.domain.AudioAlarm
import com.aconno.sensorics.domain.telephony.CallStateListener
import com.aconno.sensorics.domain.telephony.DeviceTelephonyManager
import dagger.android.DaggerService
import timber.log.Timber
import javax.inject.Inject


class AlarmService : DaggerService() {
    @Inject
    lateinit var notification: Notification

    @Inject
    lateinit var telephonyManager: DeviceTelephonyManager

    @Inject
    lateinit var broadcastManager: LocalBroadcastManager

    @Inject
    lateinit var audioAlarm: AudioAlarm

    /**
     * Used to check if a call gets picked up to silence the alarm
     */
    private var initialCallState: Int = 0

    private var snoozeAlarmReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Timber.e("alarm 1")
            if (action != null && action.equals(ACTION_ALARM_SNOOZE, ignoreCase = true)) {
                Timber.e("alarm 2")
                stopSelf()
            }
        }
    }

    private val callStateListener = object : CallStateListener {
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            if (state != TelephonyManager.CALL_STATE_IDLE && state != initialCallState) {
                stopSelf()
            }
        }
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            stopSelf()
            return START_NOT_STICKY
        }


        startForeground(NOTIFICATION_ID, notification)
        startAlarm()

        initialCallState = telephonyManager.getCallState()
        return START_NOT_STICKY
    }

    private fun startAlarm() {
        Timber.v("Starting alarm!")
        if(!audioAlarm.isRunning()) {
            audioAlarm.start()
        }
    }

    /**
     * Stops alarm audio and disables alarm if it not snoozed and not
     * repeating
     */
    fun stopAlarm() {
        if (audioAlarm.isRunning()) {
            Timber.v("Stopping alarm!")
            audioAlarm.stop()
        }

        broadcastManager.unregisterReceiver(snoozeAlarmReceiver)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.v("Started alarm service")
        telephonyManager.registerCallStateListener(callStateListener)

        broadcastManager.registerReceiver(snoozeAlarmReceiver, IntentFilter().apply {
            addAction(ACTION_ALARM_SNOOZE)
        })

        CpuWakeLock.acquireCpuWakeLock(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAlarm()

        telephonyManager.unregisterCallStateListener(callStateListener)

        CpuWakeLock.releaseCpuLock()
    }

    companion object {
        private val NOTIFICATION_ID = 1
    }
}