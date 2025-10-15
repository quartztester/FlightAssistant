package ru.octol1ttle.flightassistant.screen.fms.arrival

import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer.ArrivalData.MinimumsType

data class ArrivalScreenState(
    var coordinatesX: Int = 0,
    var coordinatesZ: Int = 0,
    var elevation: Int = 0,
    var minimums: Int = 0,
    var minimumsType: MinimumsType = MinimumsType.ABSOLUTE,
    var goAroundAltitude: Int = 0,
    var approachReEntryWaypointIndex: Int = 0
) {
    fun load(flightPlan: FlightPlanComputer) {
        TODO()
    }

    fun save(flightPlan: FlightPlanComputer) {
        flightPlan.arrivalData = FlightPlanComputer.ArrivalData(coordinatesX, coordinatesZ, elevation, minimums, minimumsType, goAroundAltitude, approachReEntryWaypointIndex)
    }
}