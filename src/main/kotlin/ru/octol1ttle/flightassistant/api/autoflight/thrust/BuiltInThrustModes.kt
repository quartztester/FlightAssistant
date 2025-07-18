package ru.octol1ttle.flightassistant.api.autoflight.thrust

import kotlin.math.pow
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

data class SpeedThrustMode(val target: Int) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerView): ControlInput {
        val currentThrust: Float = computers.thrust.current
        val currentSpeed: Double = computers.data.forwardVelocity.length() * 20
        val acceleration: Double = computers.data.forwardAcceleration.length() * 20

        val speedCorrection: Double = (target - currentSpeed) * FATickCounter.timePassed.pow(1.5f)
        val accelerationDamping: Double = -acceleration * FATickCounter.timePassed
        return ControlInput(
            (currentThrust + speedCorrection + accelerationDamping).toFloat().coerceIn(0.0f..1.0f),
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.thrust.speed", target)
        )
    }
}

data class SpeedReferenceVerticalMode(val targetSpeed: Int) : AutoFlightComputer.VerticalMode {
    override fun getControlInput(computers: ComputerView): ControlInput {
        // TODO: find a way to fix jittering
        val range: Float = computers.thrust.getOptimumClimbPitch() - computers.thrust.getAltitudeHoldPitch()
        val currentPitch: Float = computers.data.pitch
        val currentSpeed: Double = computers.data.forwardVelocity.length() * 20
        val acceleration: Double = computers.data.forwardAcceleration.length() * 20

        val speedCorrection: Double = (currentSpeed - targetSpeed) * FATickCounter.timePassed.pow(2) * range
        val accelerationDamping: Double = -acceleration * FATickCounter.timePassed.pow(4) * range
        return ControlInput(
            (currentPitch + speedCorrection + accelerationDamping).toFloat().coerceIn(computers.thrust.getAltitudeHoldPitch()..computers.thrust.getOptimumClimbPitch()),
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.vertical.speed_reference")
        )
    }
}