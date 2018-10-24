package com.aconno.sensorics.ui.settings.publishers.restheader

interface ItemClickListenerWithPos<in T> {
    fun onItemClick(position: Int, item: T?)
}