package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.thrust.SpeedReferenceVerticalMode
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

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

    fun getThrustInput(): ControlInput? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> ControlInput(
                departureData.takeoffThrust,
                ControlInput.Priority.NORMAL,
                Component.translatable("mode.flightassistant.thrust.takeoff"),
                identifier = ID
            )

            else -> null
        }
    }

    fun getPitchInput(): ControlInput? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> {
                SpeedReferenceVerticalMode(departureData.minimumClimbSpeed).getControlInput(computers)
            }

            else -> null
        }
    }

    fun getHeadingInput(): ControlInput? {
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
