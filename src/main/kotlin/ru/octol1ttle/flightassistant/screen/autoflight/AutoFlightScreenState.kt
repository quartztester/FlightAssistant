package ru.octol1ttle.flightassistant.screen.autoflight

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer

class AutoFlightScreenState {

    var thrustMode: ThrustMode = ThrustMode.SPEED
    var targetSpeed: Int = 15
    var climbThrustPercent: Int = 100
    var descendThrustPercent: Int = 0

    var verticalMode: VerticalMode = VerticalMode.PITCH
    var targetPitch: Float = 5.0f
    var targetAltitude: Int = 0

    var lateralMode: LateralMode = LateralMode.HEADING
    var targetHeading: Int = 360
    var targetCoordinatesX: Int = 0
    var targetCoordinatesZ: Int = 0

    fun apply(autopilotComputer: AutopilotLogicComputer) {
        autopilotComputer.thrustMode = when (thrustMode) {
            ThrustMode.SPEED -> AutopilotLogicComputer.SpeedThrustMode(targetSpeed)
            ThrustMode.VERTICAL_PROFILE -> AutopilotLogicComputer.VerticalProfileThrustMode(climbThrustPercent / 100.0f, descendThrustPercent / 100.0f)
            ThrustMode.FLIGHT_PLAN -> null
        }
        autopilotComputer.verticalMode = when (verticalMode) {
            VerticalMode.PITCH -> AutopilotLogicComputer.PitchVerticalMode(targetPitch)
            VerticalMode.ALTITUDE -> AutopilotLogicComputer.SelectedAltitudeVerticalMode(targetAltitude)
            VerticalMode.FLIGHT_PLAN -> null
        }
        autopilotComputer.lateralMode = when (lateralMode) {
            LateralMode.HEADING -> AutopilotLogicComputer.HeadingLateralMode(targetHeading)
            LateralMode.COORDINATES -> AutopilotLogicComputer.CoordinatesLateralMode(targetCoordinatesX, targetCoordinatesZ)
            LateralMode.FLIGHT_PLAN -> null
        }
    }

    enum class ThrustMode(@JvmField val displayName: Component) : NameableEnum {
        SPEED(Component.translatable("menu.flightassistant.autoflight.thrust.speed")),
        VERTICAL_PROFILE(Component.translatable("menu.flightassistant.autoflight.thrust.vertical_profile")),
        FLIGHT_PLAN(Component.translatable("menu.flightassistant.autoflight.thrust.flight_plan"));

        override fun getDisplayName(): Component {
            return this.displayName
        }
    }

    enum class VerticalMode(@JvmField val displayName: Component) : NameableEnum {
        PITCH(Component.translatable("menu.flightassistant.autoflight.vertical.pitch")),
        ALTITUDE(Component.translatable("menu.flightassistant.autoflight.vertical.altitude")),
        FLIGHT_PLAN(Component.translatable("menu.flightassistant.autoflight.vertical.flight_plan"));

        override fun getDisplayName(): Component {
            return this.displayName
        }
    }

    enum class LateralMode(@JvmField val displayName: Component) : NameableEnum {
        HEADING(Component.translatable("menu.flightassistant.autoflight.lateral.heading")),
        COORDINATES(Component.translatable("menu.flightassistant.autoflight.lateral.coordinates")),
        FLIGHT_PLAN(Component.translatable("menu.flightassistant.autoflight.lateral.flight_plan"));

        override fun getDisplayName(): Component {
            return this.displayName
        }
    }
}