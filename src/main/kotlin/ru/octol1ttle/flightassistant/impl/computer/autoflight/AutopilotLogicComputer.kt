package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

class AutopilotLogicComputer(computers: ComputerView) : Computer(computers) {
    var thrustMode: ThrustMode = ThrustMode(ThrustMode.Type.SelectedSpeed, 0.0f)
    var verticalMode: VerticalMode = VerticalMode(VerticalMode.Type.SelectedPitch, 0.0f)
    var lateralMode: LateralMode = LateralMode(LateralMode.Type.SelectedHeading, 360.0f)

    fun computeThrust(): ControlInput? {
        return when (thrustMode.type) {
            ThrustMode.Type.SelectedSpeed ->
                ControlInput(
                    computers.thrust.calculateThrustForSpeed(thrustMode.speed) ?: 0.0f,
                    ControlInput.Priority.NORMAL,
                    Text.translatable("mode.flightassistant.thrust.selected_speed", thrustMode.speed.toInt()),
                    identifier = ID
                )
            ThrustMode.Type.VerticalTarget ->
                if (!verticalMode.isAltitude()) null
                else {
                    val nearTarget: Boolean = abs(verticalMode.pitchOrAltitude - (computers.data.altitude + computers.data.velocity.y * 20)) < 10.0f
                    val useClimbThrust: Boolean = nearTarget || verticalMode.pitchOrAltitude > computers.data.altitude
                    ControlInput(
                        if (useClimbThrust) thrustMode.climbThrust!! else thrustMode.descendThrust!!,
                        ControlInput.Priority.NORMAL,
                        if (useClimbThrust) Text.translatable("mode.flightassistant.thrust.climb") else
                            if (thrustMode.descendThrust!! != 0.0f) Text.translatable("mode.flightassistant.thrust.descend")
                            else Text.translatable("mode.flightassistant.thrust.idle"),
                        identifier = ID
                    )
                }
            ThrustMode.Type.WaypointThrust -> TODO()
        }
    }

    fun computePitch(active: Boolean): ControlInput {
        return when (verticalMode.type) {
            VerticalMode.Type.SelectedPitch ->
                ControlInput(
                    verticalMode.pitchOrAltitude,
                    ControlInput.Priority.NORMAL,
                    Text.translatable("mode.flightassistant.vertical.selected_pitch", "%.1f".format(verticalMode.pitchOrAltitude)),
                    active = active,
                    identifier = ID
                )
            VerticalMode.Type.SelectedAltitude -> {
                val diff: Float = (verticalMode.pitchOrAltitude - computers.data.altitude).toFloat()
                val abs: Float = abs(diff)
                val neutralPitch: Float = computers.thrust.getAltitudeHoldPitch()

                var finalPitch: Float
                var text: Text
                if (diff >= 0) {
                    finalPitch = computers.thrust.getOptimumClimbPitch()
                    text = Text.translatable("mode.flightassistant.vertical.selected_altitude.climb", "%.0f".format(verticalMode.pitchOrAltitude))

                    val distanceFromNeutral: Float = finalPitch - neutralPitch
                    finalPitch -= distanceFromNeutral * 0.6f * ((200.0f - abs) / 100.0f).coerceIn(0.0f..1.0f)
                    finalPitch -= distanceFromNeutral * 0.4f * ((100.0f - abs) / 100.0f).coerceIn(0.0f..1.0f)
                } else {
                    finalPitch = -35.0f
                    text = Text.translatable("mode.flightassistant.vertical.selected_altitude.descend", "%.0f".format(verticalMode.pitchOrAltitude))

                    val distanceFromNeutral: Float = finalPitch - neutralPitch
                    finalPitch -= distanceFromNeutral * 0.4f * ((100.0f - abs) / 50.0f).coerceIn(0.0f..1.0f)
                    finalPitch -= distanceFromNeutral * 0.6f * ((50.0f - abs) / 50.0f).coerceIn(0.0f..1.0f)
                }

                if (abs <= 5.0f) {
                    text = Text.translatable("mode.flightassistant.vertical.selected_altitude.hold", "%.0f".format(verticalMode.pitchOrAltitude))
                }

                ControlInput(finalPitch, ControlInput.Priority.NORMAL, text, 1.5f, active, ID)
            }
            VerticalMode.Type.WaypointAltitude -> TODO()
        }
    }

    fun computeHeading(active: Boolean): ControlInput? {
        return null
    }

    override fun tick() {
    }

    override fun reset() {
    }

    class ThrustMode(var type: Type, var speed: Float, var climbThrust: Float? = null, var descendThrust: Float? = null) {
        enum class Type {
            SelectedSpeed,
            VerticalTarget,
            WaypointThrust
        }
    }

    class VerticalMode(var type: Type, var pitchOrAltitude: Float) {
        enum class Type {
            SelectedPitch,
            SelectedAltitude,
            WaypointAltitude
        }

        fun isAltitude(): Boolean {
            return type == Type.SelectedAltitude || type == Type.WaypointAltitude
        }
    }

    class LateralMode(var type: Type, var heading: Float? = null, var x: Double? = null, var z: Double? = null) {
        enum class Type {
            SelectedHeading,
            SelectedCoordinates,
            WaypointCoordinates
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("autopilot_logic")
    }
}
