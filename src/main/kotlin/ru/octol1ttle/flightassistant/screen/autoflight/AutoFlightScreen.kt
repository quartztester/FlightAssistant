package ru.octol1ttle.flightassistant.screen.autoflight

import kotlinx.coroutines.NonCancellable.children
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.network.chat.Component
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.Formatting
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.autoflight.widgets.*

class AutoFlightScreen : FABaseScreen(Component.translatable("menu.flightassistant.autoflight")) {
    private lateinit var flightDirectors: ColoredButtonWidget
    private lateinit var autoThrust: ColoredButtonWidget
    private lateinit var autopilot: ColoredButtonWidget
    private var thrustMode: ThrustModeWidget? = null
    private var verticalMode: VerticalModeWidget? = null
    private var lateralMode: LateralModeWidget? = null

    override fun init() {
        super.init()

        this.addDrawableChild(TextWidget(0, this.height / 5, this.width, 9, this.title, this.textRenderer))

        val computers: ComputerView = ComputerHost

        flightDirectors = this.addDrawableChild(ColoredButtonWidget.builder(Component.translatable("menu.flightassistant.autoflight.flight_directors")) {
            verticalMode?.applyChanges()
            lateralMode?.applyChanges()
            computers.automations.setFlightDirectors(!computers.automations.flightDirectors)
        }.position(this.centerX - 100, this.centerY + 50).width(200).build())
        autoThrust = this.addDrawableChild(ColoredButtonWidget.builder(Component.translatable("menu.flightassistant.autoflight.auto_thrust")) {
            thrustMode?.applyChanges()
            computers.automations.setAutoThrust(!computers.automations.autoThrust, false)
        }.position(this.centerX - 100, this.centerY + 80).width(95).build())
        autopilot = this.addDrawableChild(ColoredButtonWidget.builder(Component.translatable("menu.flightassistant.autoflight.autopilot")) {
            verticalMode?.applyChanges()
            lateralMode?.applyChanges()
            computers.automations.setAutoPilot(!computers.automations.autopilot, false)
        }.position(this.centerX + 5, this.centerY + 80).width(95).build())

        thrustMode?.applyChanges()
        thrustMode = this.addDrawableChild(ThrustModeWidget(computers, 5, this.height / 3, this.width / 3 - 10))
        verticalMode?.applyChanges()
        verticalMode = this.addDrawableChild(VerticalModeWidget(computers, this.width / 3 + 5, this.height / 3, this.width / 3 - 10))
        lateralMode?.applyChanges()
        lateralMode = this.addDrawableChild(LateralModeWidget(computers, this.width / 3 * 2 + 5, this.height / 3, this.width / 3 - 10))

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) { _: ButtonWidget? ->
            this.close()
        }.position(this.width - 100, this.height - 40).width(80).build())
    }

    override fun close() {
        super.close()
        for (child: Element in children()) {
            if (child is DelayedApplyChanges) {
                child.applyChanges()
            }
        }
    }

    override fun render(context: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        flightDirectors.color =
            if (ComputerHost.automations.flightDirectors) Formatting.GREEN.colorValue!!
            else Formatting.RED.colorValue!!
        autoThrust.color =
            if (ComputerHost.automations.autoThrust) Formatting.GREEN.colorValue!!
            else Formatting.RED.colorValue!!
        autopilot.color =
            if (ComputerHost.automations.autopilot) Formatting.GREEN.colorValue!!
            else Formatting.RED.colorValue!!

        super.render(context, mouseX, mouseY, delta)
    }
}
