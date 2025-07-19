package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin.SpeedReferenceVerticalMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.builtin.TakeoffThrustMode

class FlightPlanComputer(computers: ComputerView) : Computer(computers) {
    private var currentPhase: FlightPhase = FlightPhase.ON_GROUND
    var departureData: DepartureData = DepartureData()

    override fun tick() {
        currentPhase = updateFlightPhase()
    }

    private fun updateFlightPhase(): FlightPhase {
        if (computers.data.player.onGround()) {
            return FlightPhase.ON_GROUND
        }

        if (abs(computers.data.position.x - departureData.coordinatesX) < 20 && abs(computers.data.position.z - departureData.coordinatesZ) < 20) {
            return FlightPhase.TAKEOFF
        }
        if (currentPhase == FlightPhase.TAKEOFF) {
            return FlightPhase.TAKEOFF
        }

        return FlightPhase.UNKNOWN
    }

    fun getThrustMode(): AutoFlightComputer.ThrustMode? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> TakeoffThrustMode(departureData)
            else -> null
        }
    }

    fun getVerticalMode(): AutoFlightComputer.VerticalMode? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> SpeedReferenceVerticalMode(departureData.minimumClimbSpeed)
            else -> null
        }
    }

    fun getLateralMode(): AutoFlightComputer.LateralMode? {
        return null
    }

    override fun reset() {
        currentPhase = FlightPhase.UNKNOWN
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_plan")
    }

    enum class FlightPhase {
        UNKNOWN,
        ON_GROUND,
        TAKEOFF,
        CLIMB
    }

    data class DepartureData(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val elevation: Int = 0, val takeoffThrust: Float = 1.0f, val minimumClimbSpeed: Int = 15)
}
