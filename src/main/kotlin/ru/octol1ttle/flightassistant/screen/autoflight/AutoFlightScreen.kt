package ru.octol1ttle.flightassistant.screen.autoflight

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.components.TextOnlyButton

// TODO: AutoFlightScreenState to persist across reinits
// TODO: AutoFlightState for logic
class AutoFlightScreen : FABaseScreen(Component.translatable("menu.flightassistant.autoflight")) {
    private lateinit var flightDirectors: TextOnlyButton
    private lateinit var autoThrust: TextOnlyButton
    private lateinit var autopilot: TextOnlyButton

    override fun init() {
        super.init()

        flightDirectors = this.addRenderableWidget(
            TextOnlyButton(
                (this.width * (2 / 6.0)).toInt(), this.height / 4, Component.translatable("menu.flightassistant.autoflight.flight_directors.disabled")
            ) {
            computers.automations.setFlightDirectors(!computers.automations.flightDirectors)
            })
        autoThrust = this.addRenderableWidget(
            TextOnlyButton(
                (this.width * (3 / 6.0)).toInt(), this.height / 4, Component.translatable("menu.flightassistant.autoflight.auto_thrust.disabled")
            ) {
            computers.automations.setAutoThrust(!computers.automations.autoThrust, false)
            })
        autopilot = this.addRenderableWidget(
            TextOnlyButton(
                (this.width * (4 / 6.0)).toInt(), this.height / 4, Component.translatable("menu.flightassistant.autoflight.autopilot.disabled")
            ) {
            computers.automations.setAutoPilot(!computers.automations.autopilot, false)
            })
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        updateButton(flightDirectors, "menu.flightassistant.autoflight.flight_directors", computers.automations.flightDirectors)
        updateButton(autoThrust, "menu.flightassistant.autoflight.auto_thrust", computers.automations.autoThrust)
        updateButton(autopilot, "menu.flightassistant.autoflight.autopilot", computers.automations.autopilot)

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    companion object {
        private fun updateButton(button: TextOnlyButton, baseKey: String, status: Boolean) {
            button.message = Component.translatable(baseKey, Component.translatable("menu.flightassistant.autoflight.${if (status) "enabled" else "disabled"}"))
            button.color = if (status) ChatFormatting.GREEN.color!! else ChatFormatting.RED.color!!
        }
    }
}