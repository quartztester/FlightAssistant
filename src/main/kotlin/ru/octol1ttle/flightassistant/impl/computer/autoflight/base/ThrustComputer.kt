package ru.octol1ttle.flightassistant.impl.computer.autoflight.base

import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FAKeyBindings
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustChangeCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSourceRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.filterNonFaulted
import ru.octol1ttle.flightassistant.api.util.extensions.getActiveHighestPriority
import ru.octol1ttle.flightassistant.api.util.requireIn

class ThrustComputer(computers: ComputerView) : Computer(computers) {
    private val sources: MutableList<ThrustSource> = ArrayList()
    private val controllers: MutableList<FlightController> = ArrayList()

    private var lastChangeAutomatic: Boolean = false

    var current: Float = 0.0f
        private set

    var activeInput: ControlInput? = null
        private set
    var noThrustSource: Boolean = false
        private set
    var reverseUnsupported: Boolean = false
        private set
    var thrustLocked: Boolean = false
        private set

    override fun invokeEvents() {
        ThrustSourceRegistrationCallback.EVENT.invoker().register(sources::add)
        ThrustControllerRegistrationCallback.EVENT.invoker().register(controllers::add)
    }

    override fun tick() {
        val thrustSource: ThrustSource? = sources.filterNonFaulted().filter { it.isAvailable() }.minByOrNull { it.priority.value }

        val inputs: List<ControlInput> = controllers.filterNonFaulted().mapNotNull { it.getThrustInput() }.sortedBy { it.priority.value }
        val finalInput: ControlInput? = inputs.getActiveHighestPriority().maxByOrNull { it.target }

        noThrustSource = false
        reverseUnsupported = false

        if (finalInput?.active == true && !FAKeyBindings.isHoldingThrust()) {
            setTarget(finalInput.target, finalInput)
            activeInput = finalInput
            thrustLocked = false
        } else if (current == 0.0f) {
            noThrustSource = thrustSource == null && finalInput != null && finalInput.target != 0.0f
            activeInput = finalInput
            thrustLocked = false

            return
        } else {
            activeInput = null
            thrustLocked = lastChangeAutomatic
            reverseUnsupported = current < 0.0f && thrustSource?.supportsReverse == false
        }

        noThrustSource = thrustSource == null && activeInput?.target != 0.0f
        current.requireIn(-1.0f..1.0f)

        val active: Boolean = !noThrustSource && !reverseUnsupported
        activeInput = activeInput?.copy(active = active)

        if (computers.data.automationsAllowed()) {
            thrustSource?.tickThrust(current.coerceIn((if (thrustSource.supportsReverse) -1.0f else 0.0f)..1.0f))
        }
    }

    fun setTarget(target: Float, input: ControlInput? = null) {
        val oldThrust: Float = current
        if (oldThrust != target || input == null) {
            current = target.requireIn(-1.0f..1.0f)
            ThrustChangeCallback.EVENT.invoker().onThrustChange(oldThrust, current, input)
            lastChangeAutomatic = input != null
        }
    }

    fun tickTarget(sign: Float) {
        if (sign != 0.0f) {
            setTarget((current + FATickCounter.timePassed / 3 * sign).coerceIn(-1.0f..1.0f), null)
        }
    }

    fun getOptimumClimbPitch(): Float {
        val thrustSource: ThrustSource? = sources.filterNonFaulted().filter { it.isAvailable() }.minByOrNull { it.priority.value }
        if (thrustSource != null) {
            return thrustSource.optimumClimbPitch
        }

        return 55.0f
    }

    fun getAltitudeHoldPitch(): Float {
        val thrustSource: ThrustSource? = sources.filterNonFaulted().filter { it.isAvailable() }.minByOrNull { it.priority.value }
        if (thrustSource != null) {
            return thrustSource.altitudeHoldPitch
        }

        return 5.0f
    }

    fun calculateThrustForSpeed(targetSpeed: Float): Float? {
        val thrustSource: ThrustSource? = sources.filterNonFaulted().filter { it.isAvailable() }.minByOrNull { it.priority.value }
        if (thrustSource != null) {
            return thrustSource.calculateThrustForSpeed(targetSpeed)
        }

        return null
    }

    override fun reset() {
        lastChangeAutomatic = false
        current = 0.0f
        activeInput = null
        noThrustSource = false
        reverseUnsupported = false
        thrustLocked = false
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("thrust")
        const val TOGA_THRESHOLD: Float = 0.99f
    }
}
