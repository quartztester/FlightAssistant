package ru.octol1ttle.flightassistant.screen.fms

import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class DepartureScreenState() {
    var coordinatesX: Int = 0
    var coordinatesZ: Int = 0
    var elevation: Int = 0

    var takeoffThrustPercent: Int = 100
    var minimumClimbSpeed: Int = 15

    fun apply(flightPlan: FlightPlanComputer) {
        flightPlan.departureData = FlightPlanComputer.DepartureData(coordinatesX, coordinatesZ, elevation, takeoffThrustPercent / 100.0f, minimumClimbSpeed)
    }

    fun copy(): DepartureScreenState {
        val copy = DepartureScreenState()
        copy.coordinatesX = this.coordinatesX
        copy.coordinatesZ = this.coordinatesZ
        copy.elevation = this.elevation
        copy.takeoffThrustPercent = this.takeoffThrustPercent
        copy.minimumClimbSpeed = this.minimumClimbSpeed
        return copy
    }

    fun equals(other: DepartureScreenState): Boolean {
        return this.coordinatesX == other.coordinatesX
                && this.coordinatesZ == other.coordinatesZ
                && this.elevation == other.elevation
                && this.takeoffThrustPercent == other.takeoffThrustPercent
                && this.minimumClimbSpeed == other.minimumClimbSpeed
    }
}