package com.aconno.bluetooth


abstract class TasksCompleteListener {
    var tasksCompleted: Int = 0
    var tasksTotal: Int = 0
    var tasksLeft: Int = 0
    var finishable: Boolean = true

    fun onAllTasksCompleted() {
        if (finishable) onTasksComplete()
    }

    abstract fun onTasksComplete()
    fun onTaskComplete(tasksLeft: Int) {
        tasksCompleted++
        tasksTotal += if (tasksLeft - this.tasksLeft == -1) 0 else (tasksLeft - this.tasksLeft) + 1
        this.tasksLeft = tasksLeft
        onTaskComplete(tasksCompleted, tasksTotal)
    }

    abstract fun onTaskComplete(tasksCompleted: Int, tasksTotal: Int)
}