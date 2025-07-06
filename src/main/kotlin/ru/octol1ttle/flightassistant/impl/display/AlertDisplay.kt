package ru.octol1ttle.flightassistant.impl.display

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertCategory
import ru.octol1ttle.flightassistant.api.alert.CenteredAlert
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertDisplay(computers: ComputerView) : Display(computers) {
    private val withUnderline: Style = Style.EMPTY.withUnderline(true)

    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAlerts
    }

    override fun render(guiGraphics: GuiGraphics) {
        with(drawContext) {
            val x: Int = HudFrame.left + 10
            var y: Int = HudFrame.top + 5

            var renderedCentered = false
            for (category: AlertCategory in computers.alert.categories) {
                val copy: MutableText = category.categoryText.copy()
                drawText(copy.setStyle(withUnderline), x, y, (category.getFirstData { it.getAlertMethod().screen() } ?: continue).colorSupplier.invoke())
                copy.append(" ")

                var categoryRendered = false
                var lastRenderedLines = 0
                for (alert: Alert in category.activeAlerts) {
                    if (!alert.getAlertMethod().screen()) {
                        continue
                    }

                    if (!renderedCentered && alert is CenteredAlert) {
                        renderedCentered = alert.render(this, centerY + 8)
                        categoryRendered = categoryRendered || renderedCentered
                        y += fontHeight
                    }

                    if (alert is ECAMAlert) {
                        if (lastRenderedLines > 1) {
                            y += 3
                        }

                        lastRenderedLines = alert.render(this, x + getTextWidth(copy), x, y)
                        y += fontHeight
                        if (lastRenderedLines > 1) {
                            y += (fontHeight + 1) * (lastRenderedLines - 1)
                        }
                        categoryRendered = true
                    }
                }

                if (!categoryRendered) {
                    y += fontHeight
                }
                y += 3
            }
        }
    }

    override fun renderFaulted(guiGraphics: GuiGraphics) {
        with(drawContext) {
            val x: Int = HudFrame.left + 10
            val y: Int = HudFrame.top + 5

            drawText(Component.translatable("short.flightassistant.alert"), x, y, warningColor)
        }
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("alert")
    }
}
