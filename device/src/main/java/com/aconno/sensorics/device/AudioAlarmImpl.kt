package com.aconno.sensorics.device

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.telephony.TelephonyManager
import com.aconno.sensorics.domain.AudioAlarm
import com.aconno.sensorics.domain.DeviceAudioManager
import com.aconno.sensorics.domain.telephony.DeviceTelephonyManager
import timber.log.Timber

class AudioAlarmImpl(
    val context: Context,
    val telephonyManager: DeviceTelephonyManager,
    val audioManager: DeviceAudioManager
) : AudioAlarm {
    private var mediaPlayer: MediaPlayer? = null

    private val uriAlert: Uri
        get() {
            return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getValidRingtoneUri(context)
        }

    override fun start() {
        mediaPlayer = MediaPlayer().also { player ->
            player.setOnErrorListener { mp, what, extra ->
                Timber.e("Error occurred while playing audio.")
                mp.stop()
                mp.release()
                mediaPlayer = null
                true
            }

            if (telephonyManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
                player.setVolume(IN_CALL_VOLUME, IN_CALL_VOLUME)
            }
            player.setDataSource(context, uriAlert)

            if (audioManager.getAlarmStreamVolume() != 0) {
                player.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_ALARM)
                        .build()
                )
                player.isLooping = true
                player.prepare()
                player.start()
            }

            // TODO: Use vibrator
        }
    }

    override fun stop() {
        if (isRunning()) {
            mediaPlayer?.let { player ->
                player.stop()
                player.release()
            }

            mediaPlayer = null

            // TODO: Stop vibrator
        }
    }

    override fun isRunning(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    companion object {
        const val IN_CALL_VOLUME = 0.125f
    }
}