package ru.octol1ttle.flightassistant.impl.computer.autoflight

import java.time.Duration
import java.util.UUID
import kotlin.math.roundToLong
import net.minecraft.SharedConstants
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import org.joml.Vector2d
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.computer.ComputerQuery
import ru.octol1ttle.flightassistant.api.util.LimitedFIFOQueue
import ru.octol1ttle.flightassistant.api.util.extensions.distance2d
import ru.octol1ttle.flightassistant.api.util.extensions.formatRoot
import ru.octol1ttle.flightassistant.api.util.extensions.getProgressOnTrack
import ru.octol1ttle.flightassistant.api.util.extensions.vec2dFromInts
import ru.octol1ttle.flightassistant.impl.computer.autoflight.modes.*
import ru.octol1ttle.flightassistant.impl.display.StatusDisplay

class FlightPlanComputer(computers: ComputerBus) : Computer(computers) {
    var currentPhase: FlightPhase = FlightPhase.UNKNOWN
        private set

    var departureData: DepartureData = DepartureData.DEFAULT
    val enrouteData: MutableList<EnrouteWaypoint> = ArrayList()
    var arrivalData: ArrivalData = ArrivalData.DEFAULT

    var groundSpeeds: LimitedFIFOQueue<Double> = LimitedFIFOQueue(SharedConstants.TICKS_PER_SECOND * 5)

    override fun tick() {
        updateEnrouteData()
        currentPhase = updateFlightPhase()
        groundSpeeds.add(computers.data.velocityPerSecond.horizontalDistance())
    }

    private fun updateFlightPhase(): FlightPhase {
        if (!computers.data.flying) {
            if (!departureData.isDefault() && distance2d(departureData.coordinatesX, departureData.coordinatesZ, computers.data.x, computers.data.z) < 20) {
                getEnrouteOrigin()?.active = null
                getEnrouteTarget()?.active = null
                enrouteData.firstOrNull()?.active = EnrouteWaypoint.Active.TARGET
                return FlightPhase.TAKEOFF
            }
            return FlightPhase.UNKNOWN
        }

        if (currentPhase == FlightPhase.TAKEOFF) {
            if (enrouteData.isNotEmpty() && enrouteData[0].altitude - computers.data.altitude <= 10.0) {
                return FlightPhase.CLIMB
            }
            return FlightPhase.TAKEOFF
        }

        val target: EnrouteWaypoint? = getEnrouteTarget()
        if (target == null) {
            if (enrouteData.isEmpty() || arrivalData.isDefault()) {
                return FlightPhase.UNKNOWN
            }
            if (currentPhase == FlightPhase.GO_AROUND && (computers.data.altitude < arrivalData.goAroundAltitude || enrouteData.size > arrivalData.approachReEntryWaypointIndex)) {
                return FlightPhase.GO_AROUND
            }
            if (currentPhase == FlightPhase.APPROACH || currentPhase == FlightPhase.LANDING) {
                if (computers.thrust.current == 1.0f) {
                    return FlightPhase.GO_AROUND
                }
                return FlightPhase.LANDING
            }
            return FlightPhase.UNKNOWN
        }

        val cruiseAltitude: Int = getCruiseAltitude() ?: return FlightPhase.UNKNOWN
        val firstCruise: Int = enrouteData.indexOfFirst { it.altitude == cruiseAltitude }
        val lastCruise: Int = enrouteData.indexOfLast { it.altitude == cruiseAltitude }

        val targetIndex: Int = enrouteData.indexOf(target)
        if (targetIndex <= firstCruise) {
            return FlightPhase.CLIMB
        }
        if (targetIndex <= lastCruise) {
            return FlightPhase.CRUISE
        }
        if (targetIndex < enrouteData.size - 1 || arrivalData.isDefault()) {
            return FlightPhase.DESCEND
        }

        return FlightPhase.APPROACH
    }

    private fun updateEnrouteData() {
        if (currentPhase == FlightPhase.GO_AROUND && computers.data.altitude >= arrivalData.goAroundAltitude) {
            val approachReEntryWaypoint = enrouteData.getOrNull(arrivalData.approachReEntryWaypointIndex)
            approachReEntryWaypoint?.active = EnrouteWaypoint.Active.TARGET
        }

        val target: EnrouteWaypoint = getEnrouteTarget() ?: return

        if (isCloseTo(target)) {
            val targetIndex: Int = enrouteData.indexOf(target)
            val next: EnrouteWaypoint? = if (targetIndex + 1 >= enrouteData.size) null else enrouteData[targetIndex + 1]
            getEnrouteOrigin()?.active = null
            if (next != null) {
                target.active = EnrouteWaypoint.Active.ORIGIN
                next.active = EnrouteWaypoint.Active.TARGET
            } else {
                target.active = null
            }
        }
    }

    private fun getEnrouteOrigin(): EnrouteWaypoint? {
        return enrouteData.singleOrNull { it.active == EnrouteWaypoint.Active.ORIGIN }
    }

    private fun getEnrouteTarget(): EnrouteWaypoint? {
        return enrouteData.singleOrNull { it.active == EnrouteWaypoint.Active.TARGET }
    }

    private fun isCloseTo(target: EnrouteWaypoint): Boolean {
        return getDistanceToTarget(target) < computers.data.velocityPerSecond.horizontalDistance() * 3.0
    }

    fun getCruiseAltitude(): Int? {
        return enrouteData.maxOfOrNull { it.altitude }
    }

    fun getCurrentGlideSlopeTarget(): Double? {
        val origin = enrouteData.lastOrNull() ?: return null
        val originVec = vec2dFromInts(origin.coordinatesX, origin.coordinatesZ)
        val targetVec = vec2dFromInts(arrivalData.coordinatesX, arrivalData.coordinatesZ)

        val track: Vector2d = targetVec.sub(originVec)
        val trackProgress: Double = getProgressOnTrack(track, originVec, Vector2d(computers.data.x, computers.data.z))
        return Mth.lerp(trackProgress, origin.altitude.toDouble(), arrivalData.elevation.toDouble())
    }

    fun isBelowMinimums(): Boolean {
        if (currentPhase != FlightPhase.LANDING) {
            return false
        }
        val minimums =
            if (arrivalData.minimumsType == ArrivalData.MinimumsType.ABSOLUTE) arrivalData.minimums.toDouble()
            else computers.gpws.groundOrVoidY + arrivalData.minimums
        return computers.data.altitude <= minimums
    }

    fun getDistanceToTarget(target: EnrouteWaypoint): Double {
        return distance2d(target.coordinatesX, target.coordinatesZ, computers.data.x, computers.data.z)
    }

    fun getFormattedTime(distance: Double): String {
        val duration: Duration = Duration.ofSeconds((distance / groundSpeeds.average()).roundToLong())
        return if (computers.data.flying) "${duration.toMinutesPart()}:${"%02d".formatRoot(duration.toSecondsPart())}" else "--:--"
    }

    fun getThrustMode(): AutoFlightComputer.ThrustMode? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> ConstantThrustMode(departureData.takeoffThrust, Component.translatable("mode.flightassistant.thrust.takeoff"))
            FlightPhase.LANDING -> ConstantThrustMode(arrivalData.landingThrust, Component.translatable("mode.flightassistant.thrust.landing"))
            FlightPhase.GO_AROUND -> ConstantThrustMode(1.0f, Component.translatable("mode.flightassistant.thrust.toga"))
            else -> {
                val target: EnrouteWaypoint = getEnrouteTarget() ?: return null
                return if (target.speed != 0) SpeedThrustMode(target.speed) else null
            }
        }
    }

    fun getVerticalMode(): AutoFlightComputer.VerticalMode? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> {
                val target: EnrouteWaypoint = getEnrouteTarget() ?: return null
                return if (target.altitude - computers.data.altitude > 10.0) SelectedAltitudeVerticalMode(target.altitude) else null
            }
            FlightPhase.LANDING -> {
                val approach = enrouteData.lastOrNull() ?: return null
                ManagedAltitudeVerticalMode(approach.coordinatesX, approach.coordinatesZ, approach.altitude, arrivalData.coordinatesX, arrivalData.coordinatesZ, arrivalData.elevation)
                    .copy(textOverride = Component.translatable("mode.flightassistant.vertical.glide_slope"))
            }
            FlightPhase.GO_AROUND -> {
                SelectedAltitudeVerticalMode(arrivalData.goAroundAltitude, Component.translatable("mode.flightassistant.vertical.go_around"))
            }
            else -> {
                val origin: EnrouteWaypoint? = getEnrouteOrigin()
                val target: EnrouteWaypoint = getEnrouteTarget() ?: return null
                if (origin != null) ManagedAltitudeVerticalMode(origin.coordinatesX, origin.coordinatesZ, origin.altitude, target.coordinatesX, target.coordinatesZ, target.altitude)
                else SelectedAltitudeVerticalMode(target.altitude)
            }
        }
    }

    fun getLateralMode(): AutoFlightComputer.LateralMode? {
        return when (currentPhase) {
            FlightPhase.TAKEOFF -> {
                val target: EnrouteWaypoint = getEnrouteTarget() ?: return null
                TrackNavigationLateralMode(departureData.coordinatesX, departureData.coordinatesZ, target.coordinatesX, target.coordinatesZ)
            }
            FlightPhase.LANDING -> {
                val approach = enrouteData.lastOrNull() ?: return null
                TrackNavigationLateralMode(approach.coordinatesX, approach.coordinatesZ, arrivalData.coordinatesX, arrivalData.coordinatesZ)
                    .copy(textOverride = Component.translatable("mode.flightassistant.lateral.localizer"))
            }
            FlightPhase.GO_AROUND -> {
                HeadingLateralMode(computers.data.heading.toInt(), Component.translatable("mode.flightassistant.lateral.go_around"))
            }
            else -> {
                val origin: EnrouteWaypoint? = getEnrouteOrigin()
                val target: EnrouteWaypoint = getEnrouteTarget() ?: return null
                if (origin != null) TrackNavigationLateralMode(origin.coordinatesX, origin.coordinatesZ, target.coordinatesX, target.coordinatesZ)
                else DirectCoordinatesLateralMode(target.coordinatesX, target.coordinatesZ)
            }
        }
    }

    override fun <Response> handleQuery(query: ComputerQuery<Response>) {
        // TODO: config
        if (query is StatusDisplay.StatusMessageQuery && currentPhase != FlightPhase.UNKNOWN && (!departureData.isDefault() || enrouteData.isNotEmpty())) {
            query.respond(Component.translatable("status.flightassistant.flight_plan.phase.${currentPhase.name.lowercase()}"))
            val target: EnrouteWaypoint? = getEnrouteTarget()
            if (target != null) {
                val distance: Double = getDistanceToTarget(target)
                query.respond(Component.translatable("status.flightassistant.flight_plan.waypoint_distance", distance.roundToLong().toString()))
                query.respond(Component.translatable("status.flightassistant.flight_plan.waypoint_time", getFormattedTime(distance)))
            }
        }
    }

    override fun reset() {
        currentPhase = FlightPhase.UNKNOWN
        groundSpeeds.clear()
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_plan")
    }

    enum class FlightPhase {
        UNKNOWN,
        TAKEOFF,
        CLIMB,
        CRUISE,
        DESCEND,
        APPROACH,
        LANDING,
        GO_AROUND
    }

    data class DepartureData(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val elevation: Int = 0, val takeoffThrust: Float = 0.0f) {
        fun isDefault(): Boolean {
            return this == DEFAULT
        }

        companion object {
            val DEFAULT: DepartureData = DepartureData()
        }
    }

    data class EnrouteWaypoint(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val altitude: Int = 0, val speed: Int = 0, var active: Active? = null, val uuid: UUID = UUID.randomUUID()) {
        enum class Active {
            ORIGIN,
            TARGET
        }
    }

    data class ArrivalData(val coordinatesX: Int = 0, val coordinatesZ: Int = 0, val elevation: Int = 0, val landingThrust: Float = 0.0f, val minimums: Int = 0, val minimumsType: MinimumsType = MinimumsType.ABSOLUTE, val goAroundAltitude: Int = 0, val approachReEntryWaypointIndex: Int = 0) {
        enum class MinimumsType {
            ABSOLUTE,
            RELATIVE
        }

        fun isDefault(): Boolean {
            return this == DEFAULT
        }

        companion object {
            val DEFAULT: ArrivalData = ArrivalData()
        }
    }
}
