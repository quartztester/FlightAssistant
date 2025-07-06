package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer

class FlightDirectorsDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showFlightDirectors
    }

    override fun render(guiGraphics: GuiGraphics) {
        if (!computers.automations.flightDirectors) {
            return
        }
        if (computers.pitch.activeInput?.identifier != AutopilotLogicComputer.ID && computers.heading.activeInput?.identifier != AutopilotLogicComputer.ID) {
            renderFaulted()
            return
        }

        with(drawContext) {
            val halfWidth: Int = (HudFrame.width / 10.0f).toInt()

            matrices.push()
            matrices.translate(0, 0, -50)

            if (computers.pitch.activeInput?.identifier == AutopilotLogicComputer.ID) {
                val pitchY: Int = ScreenSpace.getY(computers.pitch.activeInput?.target ?: return, false) ?: return
                drawHorizontalLine(this.centerX - halfWidth, this.centerX + halfWidth, pitchY, advisoryColor)
            }

            if (computers.heading.activeInput?.identifier == AutopilotLogicComputer.ID) {
                val headingX: Int = ScreenSpace.getX(computers.heading.activeInput?.target ?: return, false) ?: return
                drawVerticalLine(headingX, this.centerY - halfWidth, this.centerY + halfWidth, advisoryColor)
            }

            matrices.pop()
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(drawContext) {
            drawMiddleAlignedText(Component.translatable("short.flightassistant.flight_directors"), centerX, HudFrame.top + 30, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("flight_directors")
    }
}
