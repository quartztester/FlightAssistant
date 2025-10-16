package ru.octol1ttle.flightassistant.impl.computer.autoflight.modes

import kotlin.math.abs
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import org.joml.Vector2d
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.PIDController
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

// TODO: tick-based plsssssssss
data class SelectedAltitudeVerticalMode(override val targetAltitude: Int) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsAltitudeMode {
    private val controller: PIDController = PIDController(1.75f, 0.125f, 0.3f, 10, -70.0f, 70.0f)
    private var lastPitchCommand: Float = 0.0f

    override fun getControlInput(computers: ComputerBus): ControlInput {
        val diff: Double = targetAltitude - computers.data.altitude
        val text: Component = if (abs(diff) <= 10.0) {
            Component.translatable("mode.flightassistant.vertical.altitude.hold")
        } else if (diff >= 0) {
            Component.translatable("mode.flightassistant.vertical.altitude.open.climb")
        } else {
            Component.translatable("mode.flightassistant.vertical.altitude.open.descend")
        }
        if (FATickCounter.ticksPassed == 0) {
            return ControlInput(lastPitchCommand, ControlInput.Priority.NORMAL, text)
        }

        var target: Float = controller.calculate(targetAltitude.toFloat(), computers.data.altitude.toFloat(), computers.data.pitch)
        if (abs(diff) > 10.0f) {
            target = if (diff > 0) target.coerceIn(0.0f..70.0f) else target.coerceIn(-70.0f..0.0f)
        }

        lastPitchCommand = target

        return ControlInput(target, ControlInput.Priority.NORMAL, text)
    }
}

data class ManagedAltitudeVerticalMode(val originX: Int, val originZ: Int, val originAltitude: Int, val targetX: Int, val targetZ: Int, override val targetAltitude: Int) : AutoFlightComputer.VerticalMode, AutoFlightComputer.FollowsAltitudeMode {
    private val controller: PIDController = PIDController(1.75f, 0.125f, 0.3f, 10, -70.0f, 70.0f)
    private var lastPitchCommand: Float = 0.0f

    override fun getControlInput(computers: ComputerBus): ControlInput {
        val totalDiff: Double = targetAltitude - computers.data.altitude
        val text: Component =
            if (abs(totalDiff) <= 10.0)
                if (targetAltitude == computers.plan.getCruiseAltitude()) Component.translatable("mode.flightassistant.vertical.altitude.cruise")
                else Component.translatable("mode.flightassistant.vertical.altitude.hold")
            else if (totalDiff > 0) Component.translatable("mode.flightassistant.vertical.altitude.managed.climb")
            else Component.translatable("mode.flightassistant.vertical.altitude.managed.descend")

        if (FATickCounter.ticksPassed == 0) {
            return ControlInput(lastPitchCommand, ControlInput.Priority.NORMAL, text)
        }

        val origin: Vector2d = vec2dFromInts(originX, originZ)
        val track: Vector2d = vec2dFromInts(targetX, targetZ).sub(origin)
        val trackProgress: Double = getProgressOnTrack(track, origin, Vector2d(computers.data.x, computers.data.z))
        val trackDiff: Int = targetAltitude - originAltitude

        val timeToTarget: Double = track.length() / computers.data.velocityPerSecond.horizontalDistance()
        val targetVerticalSpeed: Double = trackDiff / timeToTarget
        val currentVerticalSpeed: Double = computers.data.velocityPerSecond.y

        val currentTarget: Double = Mth.lerp(trackProgress, originAltitude.toDouble(), targetAltitude.toDouble())
        val currentDiff: Double = currentTarget - computers.data.altitude

        val target: Float = controller.calculate((targetVerticalSpeed + currentDiff).toFloat(), currentVerticalSpeed.toFloat(), computers.data.pitch)
        lastPitchCommand = target

        return ControlInput(target, ControlInput.Priority.NORMAL, text)
    }
}