package ru.octol1ttle.flightassistant.screen

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.screen.widgets.ColoredButtonWidget
import ru.octol1ttle.flightassistant.screen.widgets.autoflight.DelayedApplyChanges
import ru.octol1ttle.flightassistant.screen.widgets.autoflight.LateralModeWidget
import ru.octol1ttle.flightassistant.screen.widgets.autoflight.ThrustModeWidget
import ru.octol1ttle.flightassistant.screen.widgets.autoflight.VerticalModeWidget

class AutoFlightScreen : FABaseScreen(Text.translatable("menu.flightassistant.autoflight")) {
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

        flightDirectors = this.addDrawableChild(ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.flight_directors")) {
            computers.automations.setFlightDirectors(!computers.automations.flightDirectors)
        }.position(this.centerX - 100, this.centerY + 50).width(200).build())
        autoThrust = this.addDrawableChild(ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.auto_thrust")) {
            thrustMode?.applyChanges()
            computers.automations.setAutoThrust(!computers.automations.autoThrust, true)
        }.position(this.centerX - 100, this.centerY + 80).width(95).build())
        autopilot = this.addDrawableChild(ColoredButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight.autopilot")) {
            computers.automations.setAutoPilot(!computers.automations.autopilot, true)
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

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        flightDirectors.color =
            if (ComputerHost.automations.flightDirectors) Formatting.GREEN.colorValue!!
            else 0xFFFFFF
        autoThrust.color =
            if (ComputerHost.automations.autoThrust) Formatting.GREEN.colorValue!!
            else 0xFFFFFF
        autopilot.color =
            if (ComputerHost.automations.autopilot) Formatting.GREEN.colorValue!!
            else 0xFFFFFF

        super.render(context, mouseX, mouseY, delta)
    }
}
