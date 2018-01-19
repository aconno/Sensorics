package de.troido.acnsensa.data

data class DescriptiveStats<out T>(val min: T, val max: T, val avg: T)
