package ru.octol1ttle.flightassistant.impl.computer

import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.ModuleController
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.*
import ru.octol1ttle.flightassistant.impl.computer.autoflight.base.*
import ru.octol1ttle.flightassistant.impl.computer.safety.*
import ru.octol1ttle.flightassistant.mixin.ClientLevelRunningNormallyInvoker

internal object ComputerHost : ModuleController<Computer>, ComputerView {
    private val computers: MutableMap<ResourceLocation, Computer> = LinkedHashMap()

    override fun isEnabled(identifier: ResourceLocation): Boolean {
        return get(identifier).enabled
    }

    override fun isFaulted(identifier: ResourceLocation): Boolean {
        return get(identifier).faulted
    }

    override fun setEnabled(identifier: ResourceLocation, enabled: Boolean): Boolean {
        val computer: Computer = get(identifier)

        val oldEnabled: Boolean = computer.enabled
        computer.enabled = enabled
        computer.reset()

        return oldEnabled
    }

    fun getFaultCount(identifier: ResourceLocation): Int {
        return get(identifier).faultCount
    }

    override fun identifiers(): Set<ResourceLocation> {
        return computers.keys
    }

    override fun register(identifier: ResourceLocation, module: Computer) {
        if (FlightAssistant.initComplete) {
            throw IllegalStateException("Initialization is already complete, but trying to register a computer with identifier: $identifier")
        }
        if (computers.containsKey(identifier)) {
            throw IllegalArgumentException("Already registered computer with identifier: $identifier")
        }
        computers[identifier] = module
    }

    private fun registerBuiltin() {
        register(AirDataComputer.ID, AirDataComputer(this, mc))
        register(FlightProtectionsComputer.ID, FlightProtectionsComputer(this))

        register(StallComputer.ID, StallComputer(this))
        register(VoidProximityComputer.ID, VoidProximityComputer(this))
        register(GroundProximityComputer.ID, GroundProximityComputer(this))
        register(ElytraStatusComputer.ID, ElytraStatusComputer(this))
        register(ChunkStatusComputer.ID, ChunkStatusComputer(this))

        register(FlightPlanComputer.ID, FlightPlanComputer(this))
        register(AutomationsComputer.ID, AutomationsComputer(this))
        register(AutopilotLogicComputer.ID, AutopilotLogicComputer(this))
        register(FireworkComputer.ID, FireworkComputer(this, mc))
        register(PitchComputer.ID, PitchComputer(this))
        register(HeadingComputer.ID, HeadingComputer(this))
        register(RollComputer.ID, RollComputer(this))
        register(ThrustComputer.ID, ThrustComputer(this))

        register(AlertComputer.ID, AlertComputer(this, mc.soundManager))
    }

    internal fun sendRegistrationEvent() {
        registerBuiltin()
        ComputerRegistrationCallback.EVENT.invoker().register(this, this::register)
        for (computer: Computer in computers.values) {
            computer.subscribeToEvents()
        }
        for (computer: Computer in computers.values) {
            computer.invokeEvents()
        }

        logRegisterComplete()
    }

    private fun logRegisterComplete() {
        val namespaces = ArrayList<String>()
        for (id: ResourceLocation in computers.keys) {
            if (!namespaces.contains(id.namespace)) {
                namespaces.add(id.namespace)
            }
        }
        FlightAssistant.logger.info(
            "Registered {} computers from mods: {}",
            computers.size,
            namespaces.joinToString(", ")
        )
    }

    internal fun tick(tickDelta: Float) {
        val paused: Boolean = mc.isPaused /*? if >=1.21 {*/ || !(mc as ClientLevelRunningNormallyInvoker).invokeIsLevelRunningNormally() //?}
        FATickCounter.tick(mc.player!!, tickDelta, paused)
        if (paused || FATickCounter.ticksSinceWorldLoad < FATickCounter.worldLoadWaitTime || !FAConfig.global.modEnabled) {
            return
        }

        for ((id: ResourceLocation, computer: Computer) in computers) {
            if (computer.enabled) {
                try {
                    computer.tick()
                    computer.faulted = false
                } catch (t: Throwable) {
                    computer.faulted = true
                    computer.faultCount++

                    computer.enabled = false
                    computer.reset()

                    FlightAssistant.logger.atError().setCause(t)
                        .log("Exception ticking computer with identifier: {}", id)
                }
            }
        }
    }

    override fun get(identifier: ResourceLocation): Computer {
        return computers[identifier] ?: throw IllegalArgumentException("No computer registered with ID: $identifier")
    }
}
