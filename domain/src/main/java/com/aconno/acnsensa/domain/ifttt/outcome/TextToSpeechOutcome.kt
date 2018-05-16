//package com.aconno.acnsensa.domain.ifttt.outcome
//
//import com.aconno.acnsensa.domain.ifttt.TextToSpeechPlayer
//
//class TextToSpeechOutcome(
//    val text: String,
//    private val textToSpeechPlayer: TextToSpeechPlayer
//) : Outcome {
//
//    override fun execute() {
//        textToSpeechPlayer.play(text)
//    }
//}