package ru.octol1ttle.flightassistant.api.display

import net.minecraft.client.gui.GuiGraphics
import ru.octol1ttle.flightassistant.api.computer.ComputerBus

/**
 *
 * A class responsible for presenting data on the HUD. Should *not* be used for computing data, do this in a [ru.octol1ttle.flightassistant.api.computer.Computer] instead
 */
abstract class Display(val computers: ComputerBus) {
    var enabled: Boolean = true
    var faulted: Boolean = false
    var faultCount: Int = 0

    abstract fun allowedByConfig(): Boolean
    abstract fun render(guiGraphics: GuiGraphics)
    abstract fun renderFaulted(guiGraphics: GuiGraphics)
}
