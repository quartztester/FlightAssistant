package ru.octol1ttle.flightassistant.screen.fms

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class EnrouteScreen : FABaseScreen(Component.translatable("menu.flightassistant.fms.departure")) {
    private lateinit var discardChanges: Button

    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, 7, this.width, 9, this.title, this.font))

        discardChanges = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.discard_changes")) { _: Button? ->
            state = lastState.copy()
            this.rebuildWidgets()
        }.pos(this.width - 200, this.height - 30).width(100).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            lastState = state.copy()
            state.apply(computers.plan)
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        discardChanges.active = !state.equals(lastState)

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    companion object {
        var lastState: EnrouteScreenState = EnrouteScreenState()
        var state: EnrouteScreenState = EnrouteScreenState()
    }
}