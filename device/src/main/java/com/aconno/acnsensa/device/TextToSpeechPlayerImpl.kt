package com.aconno.acnsensa.device

import android.content.Context
import android.speech.tts.TextToSpeech
import com.aconno.acnsensa.domain.ifttt.TextToSpeechPlayer

class TextToSpeechPlayerImpl(private val context: Context) :
    TextToSpeechPlayer, TextToSpeech.OnInitListener {

    private lateinit var text: String
    private lateinit var textToSpeech: TextToSpeech

    override fun play(text: String) {
        this.text = text
        textToSpeech = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speak")
        //textToSpeech.shutdown()
    }
}
