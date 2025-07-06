package ru.octol1ttle.flightassistant.api.util.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput

class EntityTurnEvents private constructor() {
    companion object {
        @JvmField
        val X_ROT: Event<ChangeLookDirection> = EventFactory.createLoop()
        @JvmField
        val Y_ROT: Event<ChangeLookDirection> = EventFactory.createLoop()
    }

    fun interface ChangeLookDirection {
        fun onChangeLookDirection(delta: Float, output: MutableList<ControlInput>)
    }
}
