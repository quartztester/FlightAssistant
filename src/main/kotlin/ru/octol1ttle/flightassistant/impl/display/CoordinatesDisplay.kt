package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.lineHeight
import ru.octol1ttle.flightassistant.api.util.extensions.primaryColor
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.config.FAConfig

class CoordinatesDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showCoordinates
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.left + 10
            val y: Int = HudFrame.bottom - 19

            drawString("X: ${computers.hudData.lerpedPosition.x.roundToInt()}${getDirectionSignX(computers.data.heading)}", x, y, primaryColor)
            drawString("Z: ${computers.hudData.lerpedPosition.z.roundToInt()}${getDirectionSignZ(computers.data.heading)}", x, y + lineHeight, primaryColor)
        }
    }

    private fun getDirectionSignX(heading: Float): String {
        if (heading in 30.0..150.0) {
            return " (+)"
        }

        if (heading in 210.0..330.0) {
            return " (-)"
        }

        return ""
    }

    private fun getDirectionSignZ(heading: Float): String {
        if (heading >= 300 || heading <= 60) {
            return " (-)"
        }

        if (heading in 120.0..240.0) {
            return " (+)"
        }

        return ""
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(guiGraphics) {
            val x: Int = HudFrame.left + 10
            val y: Int = HudFrame.bottom - 19

            drawString("X", x, y, warningColor)
            drawString("Z", x, y + lineHeight, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("coordinates")
    }
}
