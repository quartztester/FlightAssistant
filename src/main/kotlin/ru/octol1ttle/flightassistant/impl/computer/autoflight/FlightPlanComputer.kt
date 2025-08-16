package ru.octol1ttle.flightassistant.impl.computer.autoflight

import kotlin.math.abs
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import org.joml.Vector2d
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.DirectCoordinatesLateralMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.SpeedReferenceVerticalMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.TakeoffThrustMode
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.TrackNavigationLateralMode
import ru.octol1ttle.flightassistant.impl.display.StatusDisplay

class FlightPlanComputer(computers: ComputerBus) : Computer(computers) {
    var currentPhase: FlightPhase = FlightPhase.ON_GROUND
        private set
    var departureData: DepartureData = DepartureData.DEFAULT
    val enrouteData: MutableList<EnrouteWaypoint> = ArrayList()

    override fun tick() {
        currentPhase = updateFlightPhase()
        updateEnrouteData()
    }

    private fun updateFlightPhase(): FlightPhase {
        if (!computers.data.flying) {
            if (!departureData.isDefault() && abs(departureData.coordinatesX - computers.data.position.x) < 20 && abs(departureData.coordinatesZ - computers.data.position.z) < 20) {
                return FlightPhase.TAKEOFF
            }
            return FlightPhase.ON_GROUND
        }

        if (currentPhase == FlightPhase.TAKEOFF) {
            if (enrouteData.isNotEmpty() && computers.data.altitude - (computers.data.groundY ?: computers.data.voidY.toDouble()) >= 15) {
                return FlightPhase.CLIMB
            }
            return FlightPhase.TAKEOFF
        }

        if (currentPhase == FlightPhase.CLIMB) {
            return FlightPhase.CLIMB
        }

        return FlightPhase.UNKNOWN
    }

    private fun updateEnrouteData() {
        if (enrouteData.isEmpty()) {
            return
        }

        val target: EnrouteWaypoint? = enrouteData.singleOrNull { it.active == EnrouteWaypoint.Active.TARGET }
        if (target != null && Vector2d.distance(target.coordinatesX.toDouble(), target.coordinatesZ.toDouble(), computers.data.position.x, computers.data.position.z) < computers.data.velocityPerSecond.horizontalDistance() * 3.0) {
            val targetIndex: Int = enrouteData.indexOf(target)
            val next: EnrouteWaypoint? = if (targetIndex + 1 >= enrouteData.size) null else enrouteData[targetIndex + 1]
            enrouteData.singleOrNull { it.active == EnrouteWaypoint.Active.ORIGIN }?.active = null
            if (next != null) {
                target.active = EnrouteWaypoint.Active.ORIGIN
                next.active = EnrouteWaypoint.Active.TARGET
            } else {
                target.active = null
            }
        }
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
        return when (currentPhase) {
            FlightPhase.UNKNOWN -> null
            FlightPhase.TAKEOFF -> {
                if (enrouteData.isEmpty()) return null
                TrackNavigationLateralMode(departureData.coordinatesX, departureData.coordinatesZ, enrouteData[0].coordinatesX, enrouteData[0].coordinatesZ)
            }

            FlightPhase.CLIMB -> {
                if (enrouteData.isEmpty()) return null
                val origin: EnrouteWaypoint? = enrouteData.singleOrNull { it.active == EnrouteWaypoint.Active.ORIGIN }
                val target: EnrouteWaypoint = enrouteData.singleOrNull { it.active == EnrouteWaypoint.Active.TARGET } ?: return null
                if (origin != null) TrackNavigationLateralMode(origin.coordinatesX, origin.coordinatesZ, target.coordinatesX, target.coordinatesZ)
                else DirectCoordinatesLateralMode(target.coordinatesX, target.coordinatesZ)
            }
            else -> null
        }
    }

    override fun <Response> processQuery(query: ComputerQuery<Response>) {
        if (query is StatusDisplay.StatusMessageQuery && currentPhase != FlightPhase.UNKNOWN && (!departureData.isDefault() || enrouteData.isNotEmpty())) {
            query.respond(Component.translatable("status.flightassistant.flight_phase.${currentPhase.name.lowercase()}"))
        }
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

    data class DepartureData(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val elevation: Int = 0, val takeoffThrust: Float = 1.0f, val minimumClimbSpeed: Int = 15) {
        fun isDefault(): Boolean {
            return this == DEFAULT
        }

        companion object {
            val DEFAULT: DepartureData = DepartureData()
        }
    }

    data class EnrouteWaypoint(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val altitude: Int = 0, val speed: Int = 0, var active: Active? = null) {
        enum class Active {
            ORIGIN,
            TARGET
        }
    }
}
