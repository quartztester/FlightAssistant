package ru.octol1ttle.flightassistant.impl.computer.autoflight.modes

import net.minecraft.network.chat.Component
import org.joml.Vector2d
import org.joml.Vector2i
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.pointsToDirection
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutoFlightComputer

data class HeadingLateralMode(override val targetHeading: Int) : AutoFlightComputer.LateralMode, AutoFlightComputer.FollowsHeadingMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            targetHeading.toFloat(),
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.lateral.heading")
        )
    }
}

data class DirectCoordinatesLateralMode(override val targetX: Int, override val targetZ: Int) : AutoFlightComputer.LateralMode, AutoFlightComputer.FollowsCoordinatesMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        return ControlInput(
            pointsToDirection(targetX.toDouble(), targetZ.toDouble(), computers.data.position.x, computers.data.position.z).toFloat() + 180.0f,
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.lateral.direct_coordinates")
        )
    }
}

data class TrackNavigationLateralMode(val originX: Int, val originZ: Int, override val targetX: Int, override val targetZ: Int) : AutoFlightComputer.LateralMode, AutoFlightComputer.FollowsCoordinatesMode {
    override fun getControlInput(computers: ComputerBus): ControlInput {
        val targetCoordinates: Vector2d = getTargetCoordinates(computers)
        return ControlInput(
            pointsToDirection(targetCoordinates.x, targetCoordinates.y, computers.data.position.x, computers.data.position.z).toFloat() + 180.0f,
            ControlInput.Priority.NORMAL,
            Component.translatable("mode.flightassistant.lateral.track_navigation")
        )
    }

    private fun getTargetCoordinates(computers: ComputerBus): Vector2d {
        val track = Vector2d(Vector2i(targetX - originX, targetZ - originZ))
        val trackLengthSquared: Double = track.lengthSquared()
        if (trackLengthSquared == 0.0) {
            return Vector2d(targetX.toDouble(), targetZ.toDouble())
        }

        val fromStart = Vector2d(computers.data.position.x - originX, computers.data.position.z - originZ)
        val trackProgress: Double = (fromStart.x * track.x + fromStart.y * track.y) / trackLengthSquared
        if (trackProgress < 0) {
            return Vector2d(originX.toDouble(), originZ.toDouble())
        }
        if (trackProgress > 1) {
            return Vector2d(targetX.toDouble(), targetZ.toDouble())
        }
        val closestTrackPoint = Vector2d(originX + trackProgress * track.x, originZ + trackProgress * track.y)
        val trackNormalized: Vector2d = track.normalize()
        return closestTrackPoint.add(trackNormalized.mul(computers.data.velocityPerSecond.horizontalDistance() * 3.0))
    }
}