package de.troido.acnsensa.data

data class AxisVector<out T : AxisComponent>(val x: T, val y: T, val z: T) {
    operator fun get(axis: Axis): T = when (axis) {
        Axis.X -> x
        Axis.Y -> y
        Axis.Z -> z
    }

    fun asList(): List<T> =
            listOf(x, y, z)
}
