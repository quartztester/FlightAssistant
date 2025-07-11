package ru.octol1ttle.flightassistant.screen.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.Style
import net.minecraft.util.Mth
import ru.octol1ttle.flightassistant.api.util.extensions.font

class TextOnlyButton(val baseX: Int, y: Int, text: Component, onPress: OnPress) : Button(baseX - font.width(text) / 2, y, font.width(text), font.lineHeight, text, onPress, DEFAULT_NARRATION) {
    var color: Int = 0

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val message: Component = if (this.isHovered) {
            ComponentUtils.mergeStyles(this.message.copy(), Style.EMPTY.withUnderlined(true))
        } else {
            this.message
        }

        this.width = font.width(message)
        this.x = this.baseX - this.width / 2
        guiGraphics.drawString(font, message, this.x, this.y, this.color or (Mth.ceil(this.alpha * 255.0f) shl 24))
    }
}