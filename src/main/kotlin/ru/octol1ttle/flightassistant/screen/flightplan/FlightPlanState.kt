package ru.octol1ttle.flightassistant.screen.flightplan

interface FlightPlanState {
    fun load()

    fun needsSaving(): Boolean
    fun canSave(): Boolean
    fun save()
}
