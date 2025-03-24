package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

class FlightPlanComputer(computers: ComputerView) : Computer(computers) {
    var departureWaypoint: DepartureWaypoint? = null
    var enrouteWaypoints: List<EnrouteWaypoint>? = null
        private set
    var arrivalWaypoint: ArrivalWaypoint? = null

    override fun tick() {
    }

    fun getThrustMode(): AutopilotLogicComputer.ThrustMode? {
        return null
    }

    fun getVerticalMode(): AutopilotLogicComputer.VerticalMode? {
        return null
    }

    fun getLateralMode(): AutopilotLogicComputer.LateralMode? {
        return null
    }

    override fun reset() {
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_plan")
    }

    data class DepartureWaypoint(val x: Double, val z: Double, val takeoffThrust: Float?, val thrustReductionAltitude: Double?)

    class EnrouteWaypoint

    data class ArrivalWaypoint(val x: Double, val z: Double)
}
