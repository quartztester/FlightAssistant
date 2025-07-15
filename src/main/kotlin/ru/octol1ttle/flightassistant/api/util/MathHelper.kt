package ru.octol1ttle.flightassistant.api.util

import kotlin.math.*

fun degrees(value: Float): Float {
    return (value * (180.0f / PI)).toFloat()
}

fun degrees(value: Double): Double {
    return (value * (180.0 / PI))
}

fun radians(value: Float): Float {
    return (value * (PI / 180.0)).toFloat()
}

fun radians(value: Double): Double {
    return (value * (PI / 180.0))
}

fun furtherFromZero(value: Float): Int {
    return if (value < 0.0f) {
        floor(value).toInt()
    } else {
        ceil(value).toInt()
    }
}

fun Float.requireFinite(): Float {
    require(this.isFinite()) {
        "Float value invalid; expected finite value, got $this"
    }

    return this
}

fun Float.requireIn(range: ClosedFloatingPointRange<Float>): Float {
    this.requireFinite()
    
    require(range.contains(this)) {
        "Float value invalid; expected [${range.start}; ${range.endInclusive}], got $this"
    }

    return this
}

fun Double.requireFinite(): Double {
    require(this.isFinite()) {
        "Double value invalid; expected finite value, got $this"
    }

    return this
}

fun Double.requireIn(range: ClosedRange<Double>): Double {
    this.requireFinite()

    require(range.contains(this)) {
        "Double value invalid; expected [${range.start}; ${range.endInclusive}], got $this"
    }

    return this
}

fun findShortestPath(from: Float, to: Float, valueRange: Float): Float {
    var diff: Float = (to - from) % valueRange

    if (diff >= valueRange * 0.5) {
        diff -= valueRange
    }
    if (diff < -valueRange * 0.5) {
        diff += valueRange
    }

    return diff
}
