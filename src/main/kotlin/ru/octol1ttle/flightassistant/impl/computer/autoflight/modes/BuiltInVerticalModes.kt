package ru.octol1ttle.flightassistant.impl.computer.autoflight.modes

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.pow
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import org.joml.Vector2d
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.degrees
import ru.octol1ttle.flightassistant.api.util.extensions.getProgressOnTrack
import ru.octol1ttle.flightassistant.api.util.extensions.vec2dFromInts
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

data class PitchVerticalMode(override val targetPitch: Float) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsPitchMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            targetPitch,
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.vertical.pitch")
        )
    }
}

data class SpeedReferenceVerticalMode(override val targetSpeed: Int) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsSpeedMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        // TODO: find a way to fix jittering
        val range: Float = computers.thrust.getOptimumClimbPitch() - computers.thrust.getAltitudeHoldPitch()
        val currentPitch: Float = computers.data.pitch
        val currentSpeed: Double = computers.data.forwardVelocityPerSecond.length()
        val acceleration: Double = computers.data.forwardAcceleration * 20

        val speedCorrection: Double = (currentSpeed - targetSpeed) * FATickCounter.timePassed.pow(1.5f) * range
        val accelerationDamping: Double = acceleration * FATickCounter.timePassed.pow(2.0f) * range
        return ControlInput(
            (currentPitch + speedCorrection + accelerationDamping).toFloat().coerceIn(computers.thrust.getAltitudeHoldPitch()..computers.thrust.getOptimumClimbPitch()),
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.vertical.speed_reference")
        )
    }
}

data class SelectedAltitudeVerticalMode(override val targetAltitude: Int) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsAltitudeMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val diff: Double = targetAltitude - computers.data.altitude
        val abs: Double = abs(diff)
        val neutralPitch: Float = computers.thrust.getAltitudeHoldPitch()

        var finalPitch: Float
        var text: Component
        if (diff >= 0) {
            finalPitch = computers.thrust.getOptimumClimbPitch()
            text = Component.translatable("mode.flightassistant.vertical.altitude.open.climb")

            val distanceFromNeutral: Float = finalPitch - neutralPitch
            finalPitch -= distanceFromNeutral * 0.6f * ((200.0 - abs) / 100.0).coerceIn(0.0..1.0).toFloat()
            finalPitch -= distanceFromNeutral * 0.4f * ((100.0 - abs) / 100.0).coerceIn(0.0..1.0).toFloat()
        } else {
            finalPitch = -35.0f
            text = Component.translatable("mode.flightassistant.vertical.altitude.open.descend")

            val distanceFromNeutral: Float = finalPitch - neutralPitch
            finalPitch -= distanceFromNeutral * 0.4f * ((100.0 - abs) / 50.0).coerceIn(0.0..1.0).toFloat()
            finalPitch -= distanceFromNeutral * 0.6f * ((50.0 - abs) / 50.0).coerceIn(0.0..1.0).toFloat()
        }

        if (abs <= 5.0f) {
            text = Component.translatable("mode.flightassistant.vertical.altitude.hold")
        }

        return ControlInput(finalPitch, ControlInput.Priority.NORMAL, text)
    }
}

data class ManagedAltitudeVerticalMode(val originX: Int, val originZ: Int, val originAltitude: Int, val targetX: Int, val targetZ: Int, override val targetAltitude: Int) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsAltitudeMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val origin: Vector2d = vec2dFromInts(originX, originZ)
        val track: Vector2d = vec2dFromInts(targetX, targetZ).sub(origin)
        val trackProgress: Double = getProgressOnTrack(track, origin, Vector2d(computers.data.x, computers.data.z))

        val currentTarget: Double = Mth.lerp(trackProgress, originAltitude.toDouble(), targetAltitude.toDouble())
        val currentDiff: Double = currentTarget - computers.data.altitude

        val neutralPitch: Float = computers.thrust.getAltitudeHoldPitch()
        val currentTargetPitch: Float = neutralPitch + degrees(atan2(currentDiff, computers.data.velocityPerSecond.horizontalDistance() * 5.0)).toFloat()

        val totalDiff: Int = targetAltitude - originAltitude
        val finalPitch: Float =
            if (totalDiff > 5) currentTargetPitch.coerceIn(neutralPitch..computers.thrust.getOptimumClimbPitch())
            else if (totalDiff < -5) currentTargetPitch.coerceIn(-35.0f..-2.2f)
            else currentTargetPitch

        val text: Component =
            if (abs(totalDiff) < 5) Component.translatable("mode.flightassistant.vertical.altitude.hold")
            else if (totalDiff > 0) Component.translatable("mode.flightassistant.vertical.altitude.managed.climb")
            else Component.translatable("mode.flightassistant.vertical.altitude.managed.descend")

        return ControlInput(finalPitch, ControlInput.Priority.NORMAL, text)
    }
}