package ru.octol1ttle.flightassistant.impl.alert.gpws

import kotlin.math.max
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.CenteredAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.FATickCounter.totalTicks
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.centerX
import ru.octol1ttle.flightassistant.api.util.extensions.drawHighlightedCenteredText
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.config.options.SafetyOptions
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class TooLowTerrainAlert(computers: ComputerBus) : Alert(computers), CenteredAlert {
    override val data: AlertData
        get() = AlertData.TOO_LOW_TERRAIN

    private var maxRadarAltitude: Double = 0.0

    override fun shouldActivate(): Boolean {
        if (!FAConfig.safety.unsafeTerrainClearanceAlert) {
            return false
        }

        val altitudeAboveGround = computers.data.altitude - (computers.data.groundY ?: return false)
        maxRadarAltitude = max(maxRadarAltitude, altitudeAboveGround)
        if (maxRadarAltitude < 15) {
            return false
        }

        return when (computers.plan.currentPhase) {
            FlightPlanComputer.FlightPhase.UNKNOWN,
            FlightPlanComputer.FlightPhase.TAKEOFF,
            FlightPlanComputer.FlightPhase.GO_AROUND -> false
            FlightPlanComputer.FlightPhase.CLIMB,
            FlightPlanComputer.FlightPhase.CRUISE,
            FlightPlanComputer.FlightPhase.DESCEND,
            FlightPlanComputer.FlightPhase.APPROACH -> {
                altitudeAboveGround < 15
            }
            FlightPlanComputer.FlightPhase.LANDING -> {
                val glideSlopeDeviation = computers.plan.getCurrentGlideSlopeTarget()!! - computers.data.altitude
                return altitudeAboveGround < glideSlopeDeviation
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, y: Int): Boolean {
        guiGraphics.drawHighlightedCenteredText(Component.translatable("alert.flightassistant.gpws.too_low_terrain"), guiGraphics.centerX, y, cautionColor, totalTicks % 40 >= 20)
        return true
    }

    override fun getAlertMethod(): SafetyOptions.AlertMethod {
        return FAConfig.safety.unsafeTerrainClearanceAlertMethod
    }
}