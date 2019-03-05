package com.aconno.bluetooth.tasks

import java.util.*

abstract class Task(var active: Boolean = false) {
    val taskQueue: Queue<Task> = ArrayDeque<Task>()

    abstract fun onError(e: Exception)

    companion object {
        const val RETRIES_ALLOWED = 5
    }
}