package com.aconno.sensorics.ui.settings.publishers.rheader

interface ItemClickListenerWithPos<in T> {
    fun onItemClick(position: Int, item: T?)
}