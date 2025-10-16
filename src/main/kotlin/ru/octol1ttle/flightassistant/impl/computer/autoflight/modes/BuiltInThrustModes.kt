package ru.octol1ttle.flightassistant.impl.computer.autoflight.modes

import kotlin.math.abs
import kotlin.math.pow
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

data class TakeoffThrustMode(val data: FlightPlanComputer.DepartureData) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            data.takeoffThrust,
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.thrust.takeoff")
        )
    }
}

data class LandingThrustMode(val data: FlightPlanComputer.ArrivalData) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            data.landingThrust,
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.thrust.landing")
        )
    }
}

data class SpeedThrustMode(override val targetSpeed: Int) : AutoFlightComputer.ThrustMode, AutoFlightComputer.FollowsSpeedMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val currentThrust: Float = computers.thrust.current
        if (FATickCounter.ticksPassed == 0) {
            return ControlInput(currentThrust, ControlInput.Priority.NORMAL, Component.translatable("mode.flightassistant.thrust.speed"))
        }
        val currentSpeed: Double = computers.data.forwardVelocityPerSecond.length()
        val acceleration: Double = computers.data.forwardAcceleration * 20.0

        val speedCorrection: Double = (targetSpeed - currentSpeed) * FATickCounter.timePassed.pow(1.5f)
        val accelerationDamping: Double = -acceleration * FATickCounter.timePassed
        return ControlInput(
            (currentThrust + speedCorrection + accelerationDamping).toFloat().coerceIn(0.0f..1.0f),
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.thrust.speed")
        )
    }
}

data class VerticalProfileThrustMode(val climbThrust: Float, val descendThrust: Float) : AutoFlightComputer.ThrustMode {
    override fun getControlInput(computers: ComputerBus): ControlInput? {
        val verticalMode: AutoFlightComputer.VerticalMode? = computers.autoflight.activeVerticalMode
        if (verticalMode !is AutoFlightComputer.FollowsAltitudeMode) {
            return null
        }
        val nearTarget: Boolean = abs(verticalMode.targetAltitude - computers.data.altitude) <= 10.0f
        val useClimbThrust: Boolean = nearTarget || verticalMode.targetAltitude > computers.data.altitude
        return ControlInput(
            if (useClimbThrust) climbThrust else descendThrust,
            ControlInput.Priority.NORMAL,
            if (useClimbThrust) Component.translatable("mode.flightassistant.thrust.climb") else
                if (descendThrust != 0.0f) Component.translatable("mode.flightassistant.thrust.descend")
                else Component.translatable("mode.flightassistant.thrust.idle")
        )
    }
}