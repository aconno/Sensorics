package com.aconno.bluetooth.tasks

abstract class GenericTask : Task() {
    lateinit var internalException: Exception
    abstract fun execute()
}