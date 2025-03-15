package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

class FlightPlanComputer(computers: ComputerView) : Computer(computers) {
    var departureWaypoint: Waypoint? = null
        private set
    var enrouteWaypoints: List<Waypoint>? = null
        private set
    var arrivalWaypoint: Waypoint? = null
        private set

    override fun tick() {
        TODO("Not yet implemented")
    }

    override fun reset() {
        TODO("Not yet implemented")
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_plan")
    }

    class Waypoint {

    }
}
