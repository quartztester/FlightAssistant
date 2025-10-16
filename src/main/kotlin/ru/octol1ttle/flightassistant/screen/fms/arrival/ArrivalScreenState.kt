package ru.octol1ttle.flightassistant.screen.fms.arrival

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

data class ArrivalScreenState(
    var coordinatesX: Int = 0,
    var coordinatesZ: Int = 0,
    var elevation: Int = 0,
    var landingThrustPercent: Int = 0,
    var minimums: Int = 0,
    var minimumsType: MinimumsType = MinimumsType.ABSOLUTE,
    var goAroundAltitude: Int = 0,
    var approachReEntryWaypointIndex: Int = 0
) {
    fun load(flightPlan: FlightPlanComputer) {
        TODO()
    }

    fun save(flightPlan: FlightPlanComputer) {
        flightPlan.arrivalData = FlightPlanComputer.ArrivalData(coordinatesX, coordinatesZ, elevation, landingThrustPercent / 100.0f, minimums, minimumsType.type, goAroundAltitude, approachReEntryWaypointIndex)
    }

    enum class MinimumsType(@JvmField val displayName: Component, val type: FlightPlanComputer.ArrivalData.MinimumsType) : NameableEnum {
        ABSOLUTE(Component.translatable("menu.flightassistant.fms.arrival.minimums.absolute"), FlightPlanComputer.ArrivalData.MinimumsType.ABSOLUTE),
        RELATIVE(Component.translatable("menu.flightassistant.fms.arrival.minimums.relative"), FlightPlanComputer.ArrivalData.MinimumsType.RELATIVE);

        override fun getDisplayName(): Component {
            return this.displayName
        }
    }
}