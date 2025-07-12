package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import kotlin.math.atan2
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.degrees

// TODO: display selected shit elsewhere, not in the main bar
class AutopilotLogicComputer(computers: ComputerView) : Computer(computers) {
    var thrustMode: ThrustMode? = SpeedThrustMode(15)
    var verticalMode: VerticalMode? = PitchVerticalMode(0.0f)
    var lateralMode: LateralMode? = HeadingLateralMode(360)

    fun computeThrust(): ControlInput? {
        return when (val mode: ThrustMode? = thrustMode ?: computers.plan.getThrustMode()) {
            is SpeedThrustMode -> ControlInput(
                computers.thrust.calculateThrustForSpeed(mode.speed.toFloat()) ?: 1.0f, // TODO: smart speed follow
                ControlInput.Priority.NORMAL,
                Component.translatable("mode.flightassistant.thrust.selected_speed", mode.speed),
                identifier = ID
            )

            is VerticalProfileThrustMode -> {
                val verticalMode: VerticalMode? = verticalMode
                if (verticalMode !is SelectedAltitudeVerticalMode) {
                    return null
                }
                val nearTarget: Boolean = abs(verticalMode.altitude - computers.data.altitude) <= 5.0f
                val useClimbThrust: Boolean = nearTarget || verticalMode.altitude > computers.data.altitude
                return ControlInput(
                    if (useClimbThrust) mode.climbThrust else mode.descendThrust,
                    ControlInput.Priority.NORMAL,
                    if (useClimbThrust) Component.translatable("mode.flightassistant.thrust.climb") else
                        if (mode.descendThrust != 0.0f) Component.translatable("mode.flightassistant.thrust.descend")
                        else Component.translatable("mode.flightassistant.thrust.idle"),
                    identifier = ID
                )
            }
            null -> null
            else -> throw AssertionError()
        }
    }

    fun computePitch(active: Boolean): ControlInput? {
        return when (val mode: VerticalMode? = verticalMode ?: computers.plan.getVerticalMode()) {
            is PitchVerticalMode ->
                ControlInput(
                    mode.pitch,
                    ControlInput.Priority.NORMAL,
                    Component.translatable("mode.flightassistant.vertical.selected_pitch", "%.1f".format(mode.pitch)),
                    active = active,
                    identifier = ID
                )
            is SelectedAltitudeVerticalMode -> {
                val diff: Float = (mode.altitude - computers.data.altitude).toFloat()
                val abs: Float = abs(diff)
                val neutralPitch: Float = computers.thrust.getAltitudeHoldPitch()

                var finalPitch: Float
                var text: Component
                if (diff >= 0) {
                    finalPitch = computers.thrust.getOptimumClimbPitch()
                    text = Component.translatable("mode.flightassistant.vertical.selected_altitude.climb", mode.altitude)

                    val distanceFromNeutral: Float = finalPitch - neutralPitch
                    finalPitch -= distanceFromNeutral * 0.6f * ((200.0f - abs) / 100.0f).coerceIn(0.0f..1.0f)
                    finalPitch -= distanceFromNeutral * 0.4f * ((100.0f - abs) / 100.0f).coerceIn(0.0f..1.0f)
                } else {
                    finalPitch = -35.0f
                    text = Component.translatable("mode.flightassistant.vertical.selected_altitude.descend", mode.altitude)

                    val distanceFromNeutral: Float = finalPitch - neutralPitch
                    finalPitch -= distanceFromNeutral * 0.4f * ((100.0f - abs) / 50.0f).coerceIn(0.0f..1.0f)
                    finalPitch -= distanceFromNeutral * 0.6f * ((50.0f - abs) / 50.0f).coerceIn(0.0f..1.0f)
                }

                if (abs <= 5.0f) {
                    text = Component.translatable("mode.flightassistant.vertical.selected_altitude.hold", mode.altitude)
                }

                ControlInput(finalPitch, ControlInput.Priority.NORMAL, text, 1.5f, active, ID)
            }
            is ManagedAltitudeVerticalMode -> TODO()
            null -> null
            else -> throw AssertionError()
        }
    }

    fun computeHeading(active: Boolean): ControlInput? {
        return when (val mode: LateralMode? = lateralMode ?: computers.plan.getLateralMode()) {
            is HeadingLateralMode -> ControlInput(
                mode.heading.toFloat(),
                ControlInput.Priority.NORMAL,
                Component.translatable("mode.flightassistant.lateral.selected_heading", mode.heading),
                active = active,
                identifier = ID
            )
            is CoordinatesLateralMode -> ControlInput(
                degrees(atan2(-(mode.x - computers.data.position.x), mode.z - computers.data.position.z)).toFloat() + 180.0f,
                ControlInput.Priority.NORMAL,
                Component.translatable("mode.flightassistant.lateral.selected_coordinates", mode.x, mode.z),
                active = active,
                identifier = ID
            )
            null -> null
            else -> throw AssertionError()
        }
    }

    override fun tick() {
    }

    override fun reset() {
    }

    interface ThrustMode
    interface VerticalMode
    interface LateralMode

    data class SpeedThrustMode(val speed: Int) : ThrustMode
    data class VerticalProfileThrustMode(val climbThrust: Float, val descendThrust: Float) : ThrustMode

    data class PitchVerticalMode(val pitch: Float) : VerticalMode

    data class SelectedAltitudeVerticalMode(val altitude: Int) : VerticalMode

    data class ManagedAltitudeVerticalMode(val altitude: Int) : VerticalMode

    data class HeadingLateralMode(val heading: Int) : LateralMode

    data class CoordinatesLateralMode(val x: Int, val z: Int) : LateralMode

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("autopilot_logic")
    }
}
