package ru.octol1ttle.flightassistant.api.autoflight.pitch

import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

// TODO: Pitch Limit Hint (for smooth pitch limits and to allow diversion from actual pitch limit)
@Deprecated("Dispatch ComputerQueries instead")
interface PitchLimiter {
    fun getMinimumPitch(): ControlInput? {
        return null
    }

    fun getMaximumPitch(): ControlInput? {
        return null
    }
}
