package com.aconno.acnsensa.ui.settings.publishers.rheader

interface ItemClickListenerWithPos<in T> {
    fun onItemClick(position: Int, item: T?)
}