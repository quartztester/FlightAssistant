package ru.octol1ttle.flightassistant.screen.fms.enroute

import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class EnrouteScreenState(
    val waypoints: MutableList<EnrouteWaypointState> = ArrayList()
) {
    fun save(flightPlan: FlightPlanComputer) {
        TODO()
    }

    fun load(flightPlan: FlightPlanComputer) {
        TODO()
    }

    fun copy(): EnrouteScreenState {
        return EnrouteScreenState(ArrayList(this.waypoints))
    }

    fun equals(other: EnrouteScreenState): Boolean {
        return this.waypoints.size == other.waypoints.size && this.waypoints.all { other.waypoints.contains(it) }
    }

    data class EnrouteWaypointState(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val altitude: Int = 0, val speed: Int = 0) {
        fun toEnrouteWaypoint() {
            TODO()
        }
    }
}