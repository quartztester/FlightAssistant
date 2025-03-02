package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.joml.Vector3f
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.util.ScreenSpace
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class FlightPathDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showFlightPathVector
    }

    override fun render(drawContext: DrawContext) {
        with(drawContext) {
            val screenSpaceVec: Vector3f = ScreenSpace.getVector3f(computers.data.velocity, false) ?: return
            val trueX: Float = screenSpaceVec.x
            val trueY: Float = screenSpaceVec.y

            matrices.push()
            matrices.translate(0, 0, -100)
            fusedTranslateScale(trueX, trueY, FAConfig.display.flightPathVectorSize)

            val bodySideSize = 3
            drawVerticalLine(-bodySideSize, -bodySideSize, bodySideSize, primaryColor)
            drawVerticalLine(bodySideSize, -bodySideSize, bodySideSize, primaryColor)
            drawHorizontalLine(-bodySideSize, bodySideSize, -bodySideSize, primaryColor)
            drawHorizontalLine(-bodySideSize, bodySideSize, bodySideSize, primaryColor)

            val stabilizerSize = 5
            drawVerticalLine(0, -bodySideSize - stabilizerSize, -bodySideSize, primaryColor)

            val wingSize = 5
            drawHorizontalLine(-bodySideSize - wingSize, -bodySideSize, 0, primaryColor)
            drawHorizontalLine(bodySideSize, bodySideSize + wingSize, 0, primaryColor)

            matrices.pop()
        }
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            drawMiddleAlignedText(Text.translatable("short.flightassistant.flight_path"), centerX, centerY + 16, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("flight_path")
    }
}
