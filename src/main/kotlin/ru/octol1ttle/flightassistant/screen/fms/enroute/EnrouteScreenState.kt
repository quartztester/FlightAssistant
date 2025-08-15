package ru.octol1ttle.flightassistant.screen.fms.enroute

import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class EnrouteScreenState(
    val waypoints: MutableList<Waypoint> = ArrayList()
) {
    fun save(flightPlan: FlightPlanComputer) {
    }

    fun load(flightPlan: FlightPlanComputer) {
    }

    fun copy(): EnrouteScreenState {
        return EnrouteScreenState(this.waypoints.map { it.copy() }.toMutableList())
    }

    fun equals(other: EnrouteScreenState): Boolean {
        if (this.waypoints.size != other.waypoints.size) {
            return false
        }
        this.waypoints.forEachIndexed { i, waypoint ->
            if (other.waypoints[i] != waypoint) {
                return false
            }
        }
        return true
    }

    data class Waypoint(var coordinatesX: Int = 0, var coordinatesZ: Int = 0, var altitude: Int = 0, var speed: Int = 0, var active: FlightPlanComputer.EnrouteWaypoint.Active? = null) {
        fun toEnrouteWaypoint() {
            TODO()
        }
    }
}