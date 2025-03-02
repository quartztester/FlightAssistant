package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
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

    override fun render(drawContext: DrawContext) {
        if (!computers.automations.flightDirectors) {
            return
        }
        if (computers.pitch.activeInput?.identifier != AutopilotLogicComputer.ID && computers.heading.activeInput?.identifier != AutopilotLogicComputer.ID) {
            renderFaulted(drawContext)
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

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawMiddleAlignedText(Text.translatable("short.flightassistant.flight_directors"), centerX, HudFrame.top + 30, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_directors")
    }
}
