package ru.octol1ttle.flightassistant.api.util.extensions

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sign

fun Float.asPercentage(decimals: Int = 1): String {
    val percentage = this * 100
    val factor = 10.0.pow(decimals)

    val furtherFromZero = sign(percentage) * ceil(abs(percentage) * factor) / factor

    return "%.${decimals}f%%".formatRoot(furtherFromZero)
}