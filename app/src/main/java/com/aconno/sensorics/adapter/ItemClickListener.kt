package com.aconno.sensorics.adapter

interface ItemClickListener<in T> {
    fun onItemClick(item: T)
}