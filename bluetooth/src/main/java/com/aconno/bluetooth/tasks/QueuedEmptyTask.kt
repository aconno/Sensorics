package com.aconno.bluetooth.tasks

import java.util.*

abstract class QueuedEmptyTask : Task(UUID.randomUUID()) {
    abstract fun execute()

    override fun onError(error: Int) {}
}
