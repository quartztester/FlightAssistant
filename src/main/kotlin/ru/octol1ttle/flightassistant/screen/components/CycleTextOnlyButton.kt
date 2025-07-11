package ru.octol1ttle.flightassistant.screen.components

import dev.isxander.yacl3.api.NameableEnum
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractButton
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.Style
import ru.octol1ttle.flightassistant.api.util.extensions.font

class CycleTextOnlyButton<E : NameableEnum>(x: Int, y: Int, text: Component, entries: List<E>) : AbstractButton(x, y, font.width(text), font.lineHeight, text) {
    init {
        assert(entries.isNotEmpty()) { "Expected a list of all enum entries, got an empty list" }
    }

    var selected: E = entries.first()

    override fun onPress() {
        TODO("Not yet implemented")
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val message: Component = if (this.isHovered) {
            ComponentUtils.mergeStyles(this.message.copy(), Style.EMPTY.withUnderlined(true))
        } else {
            this.message
        }

        this.width = font.width(message)
        //guiGraphics.drawString(font, message, this.x, this.y, this.color or (Mth.ceil(this.alpha * 255.0f) shl 24))
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
        return this.defaultButtonNarrationText(narrationElementOutput)
    }
}