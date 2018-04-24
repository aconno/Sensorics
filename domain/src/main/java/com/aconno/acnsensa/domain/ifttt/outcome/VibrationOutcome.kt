//package com.aconno.acnsensa.domain.ifttt.outcome
//
//import com.aconno.acnsensa.domain.Vibrator
//
//class VibrationOutcome(private val vibrator: Vibrator) :
//    Outcome {
//
//    override fun execute() {
//        if (!running) {
//            running = true
//            val startTime = System.currentTimeMillis()
//            vibrator.vibrate(4000)
//            while (System.currentTimeMillis() - startTime < OUTCOME_EXECUTION_TIME_MS) {
//                Thread.sleep(100)
//            }
//
//            running = false
//        }
//    }
//
//    companion object {
//        var running = false
//        private const val OUTCOME_EXECUTION_TIME_MS = 8_000
//    }
//}