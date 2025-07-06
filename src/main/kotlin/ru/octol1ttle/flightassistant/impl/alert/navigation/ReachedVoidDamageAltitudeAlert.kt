package ru.octol1ttle.flightassistant.impl.alert.navigation

import net.minecraft.client.gui.DrawContext
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.alert.*
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.drawText
import ru.octol1ttle.flightassistant.api.util.extensions.warningColor
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.safety.VoidProximityComputer

class ReachedVoidDamageAltitudeAlert(computers: ComputerView) : Alert(computers), ECAMAlert {
    override val data: AlertData = AlertData.MASTER_WARNING

    override fun shouldActivate(): Boolean {
        return FAConfig.safety.voidAlertMode.warning() && computers.voidProximity.status == VoidProximityComputer.Status.REACHED_DAMAGE_ALTITUDE
    }

    override fun render(drawContext: DrawContext, firstLineX: Int, otherLinesX: Int, firstLineY: Int): Int {
        return drawContext.drawText(Component.translatable("alert.flightassistant.navigation.reached_void_damage_altitude"), firstLineX, firstLineY, warningColor)
    }
}
