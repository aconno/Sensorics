package com.aconno.sensorics.device

import android.content.Context
import android.media.AudioManager
import com.aconno.sensorics.domain.DeviceAudioManager

class DeviceAudioManagerImpl(
    context: Context
) : DeviceAudioManager {
    private val audioManager: AudioManager = context.getSystemService(
        Context.AUDIO_SERVICE
    ) as AudioManager

    override fun getAlarmStreamVolume(): Int {
        return audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
    }
}