package ru.octol1ttle.flightassistant.impl.alert.fault

import net.minecraft.client.gui.DrawContext
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.api.alert.Alert
import ru.octol1ttle.flightassistant.api.alert.AlertData
import ru.octol1ttle.flightassistant.api.alert.ECAMAlert
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.advisoryColor
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.drawText
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost

class DisplayFaultAlert(computers: ComputerView, val identifier: ResourceLocation) : Alert(computers), ECAMAlert {
    override val priorityOffset: Int = 45
    override val data: AlertData = AlertData.MASTER_CAUTION

    override fun shouldActivate(): Boolean {
        return HudDisplayHost.isFaulted(identifier)
    }

    override fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        var i = 0
        i += drawContext.drawText(Component.translatable("alert.flightassistant.fault.hud.$identifier"), firstLineX, firstLineY, cautionColor)
        i +=
            if (HudDisplayHost.countFaults(identifier) == 1) {
                drawContext.drawText(Component.translatable("alert.flightassistant.fault.hud.reset"), otherLinesX, firstLineY + 11, advisoryColor)
            } else {
                0
            }
        return i
    }
}
