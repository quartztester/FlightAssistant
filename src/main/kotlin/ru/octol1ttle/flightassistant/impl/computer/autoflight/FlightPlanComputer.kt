package ru.octol1ttle.flightassistant.impl.computer.autoflight

import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

class FlightPlanComputer(computers: ComputerView) : Computer(computers) {
    var departureWaypoint: DepartureWaypoint? = null
    var enrouteWaypoints: MutableList<EnrouteWaypoint> = ArrayList()
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
        val ID: ResourceLocation = FlightAssistant.id("flight_plan")
    }

    // TODO: Double? -> Int? (Int can do [-2b, +2b] wtf we don't need doubles)
    data class DepartureWaypoint(val x: Double, val z: Double, val takeoffThrust: Float?, val thrustReductionAltitude: Double?)

    // TODO: Double? -> Int? (Int can do [-2b, +2b] wtf we don't need doubles)
    data class EnrouteWaypoint(val x: Double, val z: Double, val altitude: Double, val thrustMode: AutopilotLogicComputer.ThrustMode)

    // TODO: Double? -> Int? (Int can do [-2b, +2b] wtf we don't need doubles)
    data class ArrivalWaypoint(val x: Double, val z: Double, val landingAltitude: Double?, val thrustMode: AutopilotLogicComputer.ThrustMode?, val minimums: Minimums?, val goAroundAltitude: Double?, val approachReentryWaypointIndex: Int?)

    data class Minimums(val type: Type, val value: Double) {
        enum class Type {
            ABSOLUTE,
            ABOVE_GROUND
        }
    }
}
