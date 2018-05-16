package com.aconno.acnsensa.adapter

interface ItemClickListener<in T> {
    fun onItemClick(item: T)
}