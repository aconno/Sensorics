package com.aconno.acnsensa.domain.ifttt

class TextToSpeechOutcome(
    val text: String,
    private val textToSpeechPlayer: TextToSpeechPlayer
) : Outcome {

    override fun execute() {
        textToSpeechPlayer.play(text)
    }
}