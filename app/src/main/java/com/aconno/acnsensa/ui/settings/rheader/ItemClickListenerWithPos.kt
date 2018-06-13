package com.aconno.acnsensa.ui.settings.rheader

interface ItemClickListenerWithPos<in T> {
    fun onItemClick(position: Int, item: T?)
}