package ru.octol1ttle.flightassistant.screen

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.screen.autoflight.AutoFlightScreen
import ru.octol1ttle.flightassistant.screen.flightplan.FlightPlanScreen
import ru.octol1ttle.flightassistant.screen.status.SystemStatusScreen

class FlightSetupScreen : FABaseScreen(Text.translatable("menu.flightassistant")) {
    override fun init() {
        super.init()

        this.addDrawableChild(TextWidget(0, centerY - 40, this.width, 9, this.title, this.textRenderer))

        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.flightassistant.flight_plan")) {
            this.client!!.setScreen(FlightPlanScreen())
        }.position(this.centerX - 175, this.centerY - 10).width(100).build())
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.flightassistant.system")) {
            this.client!!.setScreen(SystemStatusScreen())
        }.position(this.centerX + 75, this.centerY - 10).width(100).build())
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.flightassistant.autoflight")) {
            this.client!!.setScreen(AutoFlightScreen())
        }.position(this.centerX - 50, this.centerY + 30).width(100).build())

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL) { _: ButtonWidget? ->
            this.close()
        }.position(this.width - 100, this.height - 40).width(80).build())
    }
}
