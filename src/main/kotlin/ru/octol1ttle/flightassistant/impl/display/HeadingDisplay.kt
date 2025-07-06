package ru.octol1ttle.flightassistant.impl.display

import kotlin.math.roundToInt
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.math.MathHelper
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.centerX
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedText
import ru.octol1ttle.flightassistant.api.util.extensions.primaryColor
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.api.util.findShortestPath
import ru.octol1ttle.flightassistant.config.FAConfig

class HeadingDisplay(computers: ComputerView) : Display(computers) {
    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showHeadingReading || FAConfig.display.showHeadingScale
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(drawContext) {
            if (FAConfig.display.showHeadingReading) {
                renderHeadingReading()
            }

            if (FAConfig.display.showHeadingScale) {
                renderHeadingScale(centerX, HudFrame.bottom + 1)
            }
        }
    }

    private fun DrawContext.renderHeadingReading() {
        val x: Int = centerX
        val y: Int = HudFrame.bottom + 1

        val headingInt: Int = computers.data.heading.roundToInt()

        drawBorder(x - 11, y, 23, 11, primaryColor)
        drawMiddleAlignedText("%03d".format(headingInt), x, y + 2, primaryColor)
    }

    private fun DrawContext.renderHeadingScale(x: Int, y: Int) {
        val left: Int = (x - HudFrame.height * 0.5f).toInt()
        val right: Int = (x + HudFrame.height * 0.5f).toInt()

        drawHorizontalLine(left, right, y, primaryColor)
        drawVerticalLine(left, y, y + 20, primaryColor)
        drawVerticalLine(right, y, y + 20, primaryColor)

        enableScissor(left, 0, x - 12, scaledWindowHeight)
        val headingRoundedDown: Int = MathHelper.roundDownToMultiple(computers.data.heading.toDouble(), 10)
        for (i: Int in headingRoundedDown downTo -360 step 10) {
            if (!drawHeadingLine(x, y, left, right, i, computers.data.heading, true)) {
                break
            }
        }
        disableScissor()

        enableScissor(x + 13, 0, right, scaledWindowHeight)
        val headingRoundedUp: Int = MathHelper.roundUpToMultiple(computers.data.heading.roundToInt(), 10)
        for (i: Int in headingRoundedUp..720 step 10) {
            if (!drawHeadingLine(x, y, left, right, i, computers.data.heading, false)) {
                break
            }
        }
        disableScissor()
    }

    private fun DrawContext.drawHeadingLine(x: Int, y: Int, left: Int, right: Int, heading: Int, currentHeading: Float, isLeft: Boolean): Boolean {
        val textX: Int = (x + 2 * findShortestPath(currentHeading, heading.toFloat(), 360.0f)).toInt()
        if (textX < left - 100 || textX > right + 100) {
            return false
        }

        val wrappedHeading: Int = if (heading > 0) heading % 360 else 360 + heading % 360
        drawVerticalLine(textX, y, y + 3, primaryColor)
        if (wrappedHeading % 30 == 0) {
            drawMiddleAlignedText((if (wrappedHeading == 0) 360 else wrappedHeading).toString(), textX, y + 4, primaryColor)
        }

        if (wrappedHeading % 90 == 0) {
            disableScissor()
            enableScissor(left, 0, right, scaledWindowHeight)
            drawMiddleAlignedText(
                when (wrappedHeading) {
                    0, 360 -> "-Z"
                    90 -> "+X"
                    180 -> "+Z"
                    270 -> "-X"
                    else -> throw IllegalArgumentException("Degree range out of bounds: $heading")
                }, textX, y + 12, primaryColor
            )
            disableScissor()
            if (isLeft) {
                enableScissor(left, 0, x - 12, scaledWindowHeight)
            } else {
                enableScissor(x + 13, 0, right, scaledWindowHeight)
            }
        }

        return true
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(drawContext) {
            drawMiddleAlignedText(Component.translatable("short.flightassistant.heading"), centerX, HudFrame.bottom + 1, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("heading")
    }
}
