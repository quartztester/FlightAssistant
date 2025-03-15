package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.degrees

class AutopilotLogicComputer(computers: ComputerView) : Computer(computers) {
    var thrustMode: ThrustMode? = SpeedThrustMode(15.0f)
    var verticalMode: VerticalMode? = PitchVerticalMode(0.0f)
    var lateralMode: LateralMode? = HeadingLateralMode(360.0f)

    fun computeThrust(): ControlInput? {
        return when (val mode: ThrustMode? = thrustMode) {
            is SpeedThrustMode -> ControlInput(
                computers.thrust.calculateThrustForSpeed(mode.speed) ?: 0.0f,
                ControlInput.Priority.NORMAL,
                Text.translatable("mode.flightassistant.thrust.selected_speed", mode.speed.roundToInt()),
                identifier = ID
            )
            is VerticalTargetThrustMode -> {
                val verticalMode: VerticalMode? = verticalMode
                if (verticalMode !is SelectedAltitudeVerticalMode) {
                    return null
                }
                val nearTarget: Boolean = abs(verticalMode.altitude - computers.data.altitude) <= 5.0f
                val useClimbThrust: Boolean = nearTarget || verticalMode.altitude > computers.data.altitude
                return ControlInput(
                    if (useClimbThrust) mode.climbThrust else mode.descendThrust,
                    ControlInput.Priority.NORMAL,
                    if (useClimbThrust) Text.translatable("mode.flightassistant.thrust.climb") else
                        if (mode.descendThrust != 0.0f) Text.translatable("mode.flightassistant.thrust.descend")
                        else Text.translatable("mode.flightassistant.thrust.idle"),
                    identifier = ID
                )
            }
            else -> throw AssertionError()
        }
    }

    fun computePitch(active: Boolean): ControlInput? {
        return when (val mode = verticalMode) {
            is PitchVerticalMode ->
                ControlInput(
                    mode.pitch,
                    ControlInput.Priority.NORMAL,
                    Text.translatable("mode.flightassistant.vertical.selected_pitch", "%.1f".format(mode.pitch)),
                    active = active,
                    identifier = ID
                )
            is SelectedAltitudeVerticalMode -> {
                val diff: Float = (mode.altitude - computers.data.altitude).toFloat()
                val abs: Float = abs(diff)
                val neutralPitch: Float = computers.thrust.getAltitudeHoldPitch()

                var finalPitch: Float
                var text: Text
                if (diff >= 0) {
                    finalPitch = computers.thrust.getOptimumClimbPitch()
                    text = Text.translatable("mode.flightassistant.vertical.selected_altitude.climb", "%.0f".format(mode.altitude))

                    val distanceFromNeutral: Float = finalPitch - neutralPitch
                    finalPitch -= distanceFromNeutral * 0.6f * ((200.0f - abs) / 100.0f).coerceIn(0.0f..1.0f)
                    finalPitch -= distanceFromNeutral * 0.4f * ((100.0f - abs) / 100.0f).coerceIn(0.0f..1.0f)
                } else {
                    finalPitch = -35.0f
                    text = Text.translatable("mode.flightassistant.vertical.selected_altitude.descend", "%.0f".format(mode.altitude))

                    val distanceFromNeutral: Float = finalPitch - neutralPitch
                    finalPitch -= distanceFromNeutral * 0.4f * ((100.0f - abs) / 50.0f).coerceIn(0.0f..1.0f)
                    finalPitch -= distanceFromNeutral * 0.6f * ((50.0f - abs) / 50.0f).coerceIn(0.0f..1.0f)
                }

                if (abs <= 5.0f) {
                    text = Text.translatable("mode.flightassistant.vertical.selected_altitude.hold", "%.0f".format(mode.altitude))
                }

                ControlInput(finalPitch, ControlInput.Priority.NORMAL, text, 1.5f, active, ID)
            }
            is ManagedAltitudeVerticalMode -> TODO()
            else -> throw AssertionError()
        }
    }

    fun computeHeading(active: Boolean): ControlInput? {
        return when (val mode = lateralMode) {
            is HeadingLateralMode -> ControlInput(
                mode.heading,
                ControlInput.Priority.NORMAL,
                Text.translatable("mode.flightassistant.lateral.selected_heading", "%.0f".format(mode.heading)),
                active = active,
                identifier = ID
            )
            is CoordinatesLateralMode -> ControlInput(
                degrees(atan2(-(mode.x - computers.data.position.x), mode.z - computers.data.position.z)).toFloat() + 180.0f,
                ControlInput.Priority.NORMAL,
                Text.translatable("mode.flightassistant.lateral.selected_coordinates", "%.0f".format(mode.x), "%.0f".format(mode.z)),
                active = active,
                identifier = ID
            )
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

    data class SpeedThrustMode(val speed: Float) : ThrustMode
    data class VerticalTargetThrustMode(val climbThrust: Float, val descendThrust: Float) : ThrustMode

    data class PitchVerticalMode(val pitch: Float) : VerticalMode
    data class SelectedAltitudeVerticalMode(val altitude: Double) : VerticalMode
    data class ManagedAltitudeVerticalMode(val altitude: Double) : VerticalMode

    data class HeadingLateralMode(val heading: Float) : LateralMode
    data class CoordinatesLateralMode(val x: Double, val z: Double) : LateralMode

    companion object {
        val ID: Identifier = FlightAssistant.id("autopilot_logic")
    }
}
