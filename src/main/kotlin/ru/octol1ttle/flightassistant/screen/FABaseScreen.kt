package ru.octol1ttle.flightassistant.screen

import kotlin.properties.Delegates
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component

abstract class FABaseScreen(title: Component?) : Screen(title) {
    protected var centerX by Delegates.notNull<Int>()
    protected var centerY by Delegates.notNull<Int>()

    override fun init() {
        this.centerX = this.width / 2
        this.centerY = this.height / 2
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        this.renderBackground(
            guiGraphics
            /*? if >=1.21 {*//*, mouseX, mouseY, delta *///?}
        )
        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    override fun isPauseScreen(): Boolean = false
}
