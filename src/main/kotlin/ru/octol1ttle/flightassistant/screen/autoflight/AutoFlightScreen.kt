package ru.octol1ttle.flightassistant.screen.autoflight

import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.autoflight.widgets.DelayedApplyChanges
import ru.octol1ttle.flightassistant.screen.autoflight.widgets.LateralModeWidget
import ru.octol1ttle.flightassistant.screen.autoflight.widgets.ThrustModeWidget
import ru.octol1ttle.flightassistant.screen.autoflight.widgets.VerticalModeWidget

class AutoFlightScreen : FABaseScreen(Component.translatable("menu.flightassistant.autoflight")) {
    private lateinit var flightDirectors: ColoredButton
    private lateinit var autoThrust: ColoredButton
    private lateinit var autopilot: ColoredButton
    private var thrustMode: ThrustModeWidget? = null
    private var verticalMode: VerticalModeWidget? = null
    private var lateralMode: LateralModeWidget? = null

    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, this.height / 5, this.width, 9, this.title, this.font))

        val computers: ComputerView = ComputerHost

        flightDirectors = this.addRenderableWidget(ColoredButton.builder(Component.translatable("menu.flightassistant.autoflight.flight_directors")) {
            verticalMode?.applyChanges()
            lateralMode?.applyChanges()
            computers.automations.setFlightDirectors(!computers.automations.flightDirectors)
        }.pos(this.centerX - 100, this.centerY + 50).width(200).build())
        autoThrust = this.addRenderableWidget(ColoredButton.builder(Component.translatable("menu.flightassistant.autoflight.auto_thrust")) {
            thrustMode?.applyChanges()
            computers.automations.setAutoThrust(!computers.automations.autoThrust, false)
        }.pos(this.centerX - 100, this.centerY + 80).width(95).build())
        autopilot = this.addRenderableWidget(ColoredButton.builder(Component.translatable("menu.flightassistant.autoflight.autopilot")) {
            verticalMode?.applyChanges()
            lateralMode?.applyChanges()
            computers.automations.setAutoPilot(!computers.automations.autopilot, false)
        }.pos(this.centerX + 5, this.centerY + 80).width(95).build())

        thrustMode?.applyChanges()
        thrustMode = this.addRenderableWidget(ThrustModeWidget(computers, 5, this.height / 3, this.width / 3 - 10))
        verticalMode?.applyChanges()
        verticalMode = this.addRenderableWidget(VerticalModeWidget(computers, this.width / 3 + 5, this.height / 3, this.width / 3 - 10))
        lateralMode?.applyChanges()
        lateralMode = this.addRenderableWidget(LateralModeWidget(computers, this.width / 3 * 2 + 5, this.height / 3, this.width / 3 - 10))

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 100, this.height - 40).width(80).build())
    }

    override fun onClose() {
        super.onClose()
        for (child: GuiEventListener in children()) {
            if (child is DelayedApplyChanges) {
                child.applyChanges()
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        flightDirectors.color =
            if (ComputerHost.automations.flightDirectors) ChatFormatting.GREEN.color!!
            else ChatFormatting.RED.color!!
        autoThrust.color =
            if (ComputerHost.automations.autoThrust) ChatFormatting.GREEN.color!!
            else ChatFormatting.RED.color!!
        autopilot.color =
            if (ComputerHost.automations.autopilot) ChatFormatting.GREEN.color!!
            else ChatFormatting.RED.color!!

        super.render(guiGraphics, mouseX, mouseY, delta)
    }
}
