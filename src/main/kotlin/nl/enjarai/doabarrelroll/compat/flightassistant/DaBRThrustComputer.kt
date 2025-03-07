package nl.enjarai.doabarrelroll.compat.flightassistant

//? if do-a-barrel-roll {
import kotlin.math.sign
import net.minecraft.util.Identifier
import nl.enjarai.doabarrelroll.DoABarrelRoll
import nl.enjarai.doabarrelroll.api.event.ThrustEvents
import nl.enjarai.doabarrelroll.config.ModConfig
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustSource
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerView

class DaBRThrustComputer(computers: ComputerView) : Computer(computers), ThrustSource {
    override val priority: ThrustSource.Priority = ThrustSource.Priority.HIGH
    override val supportsReverse: Boolean = true
    override val optimumClimbPitch: Float = 30.0f
    override val altitudeHoldPitch: Float = 4.65f

    override fun subscribeToEvents() {
        ThrustEvents.MODIFY_THRUST_INPUT.register({
            computers.thrust.tickTarget(sign(it).toFloat())

            if (!computers.thrust.disabledOrFaulted()) {
                return@register computers.thrust.current.toDouble()
            }
            return@register it
        }, 10)
    }

    override fun isAvailable(): Boolean {
        return ModConfig.INSTANCE.enableThrust
    }

    override fun calculateThrustForSpeed(targetSpeed: Float): Float {
        return (targetSpeed / (ModConfig.INSTANCE.maxThrust * 20.0).toFloat()).coerceIn(-1.0f..1.0f)
    }

    override fun tick() {
    }

    override fun reset() {
    }

    companion object {
        val ID: Identifier = DoABarrelRoll.id("thrust")
    }
}

//?}
