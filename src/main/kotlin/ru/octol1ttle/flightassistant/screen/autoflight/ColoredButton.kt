package ru.octol1ttle.flightassistant.screen.autoflight

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component

class ColoredButton(x: Int, y: Int, width: Int, height: Int, text: Component?, onPress: OnPress?, createNarration: CreateNarration?) : Button(x, y, width, height, text, onPress, createNarration) {
    var color: Int = 0

    override fun renderString(guiGraphics: GuiGraphics, font: Font, i: Int) {
        super.renderString(guiGraphics, font, color)
    }

    @Environment(EnvType.CLIENT)
    class Builder(private val message: Component?, private val onPress: OnPress?) {
        private var tooltip: Tooltip? = null
        private var x = 0
        private var y = 0
        private var width = 150
        private var height = 20
        private var createNarration: CreateNarration?

        init {
            this.createNarration = DEFAULT_NARRATION
        }

        fun pos(i: Int, j: Int): Builder {
            this.x = i
            this.y = j
            return this
        }

        fun width(i: Int): Builder {
            this.width = i
            return this
        }

        fun size(i: Int, j: Int): Builder {
            this.width = i
            this.height = j
            return this
        }

        fun bounds(i: Int, j: Int, k: Int, l: Int): Builder {
            return this.pos(i, j).size(k, l)
        }

        fun tooltip(tooltip: Tooltip?): Builder {
            this.tooltip = tooltip
            return this
        }

        fun createNarration(createNarration: CreateNarration?): Builder {
            this.createNarration = createNarration
            return this
        }

        fun build(): ColoredButton {
            val button = ColoredButton(this.x, this.y, this.width, this.height, this.message, this.onPress, this.createNarration)
            button.tooltip = this.tooltip
            return button
        }
    }

    companion object {
        fun builder(component: Component?, onPress: OnPress?): Builder {
            return Builder(component, onPress)
        }
    }
}
