package com.aconno.acnsensa.device

import android.content.Context
import android.speech.tts.TextToSpeech
import com.aconno.acnsensa.domain.ifttt.TextToSpeechPlayer

class TextToSpeechPlayerImpl(context: Context) :
    TextToSpeechPlayer, TextToSpeech.OnInitListener {

    private val textToSpeech = TextToSpeech(context, this)

    override fun play(text: String) {
        if (!textToSpeech.isSpeaking) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speak")
        }
    }

    override fun onInit(status: Int) {
        //Do nothing.
    }
}
