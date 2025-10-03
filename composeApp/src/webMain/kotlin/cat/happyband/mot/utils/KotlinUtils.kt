package cat.happyband.mot.utils

import kotlin.math.roundToInt

fun Double.roundToDecimals(decimals: Int): Double {
    val multiplier = when (decimals) {
        0 -> 1
        1 -> 10
        2 -> 100
        3 -> 1000
        else -> 1000
    }
    return (this * multiplier).roundToInt().toDouble() / multiplier
}