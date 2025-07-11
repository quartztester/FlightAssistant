package ru.octol1ttle.flightassistant.screen.autoflight

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component

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

    enum class ThrustMode(displayName: Component) : NameableEnum {
        SPEED(Component.translatable("menu.flightassistant.autoflight.thrust.speed")),
        VERTICAL_PROFILE(Component.translatable("menu.flightassistant.autoflight.thrust.vertical_profile")),
        FLIGHT_PLAN(Component.translatable("menu.flightassistant.autoflight.thrust.flight_plan"));

        override fun getDisplayName(): Component? {
            return this.displayName
        }
    }

    enum class VerticalMode {
        PITCH,
        ALTITUDE,
        FLIGHT_PLAN
    }

    enum class LateralMode {
        HEADING,
        COORDINATES,
        FLIGHT_PLAN
    }
}