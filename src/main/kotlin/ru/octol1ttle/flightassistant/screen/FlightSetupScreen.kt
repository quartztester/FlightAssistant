package ru.octol1ttle.flightassistant.screen

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import ru.octol1ttle.flightassistant.screen.autoflight.AutoFlightScreen
import ru.octol1ttle.flightassistant.screen.status.SystemStatusScreen

class FlightSetupScreen : FABaseScreen(Component.translatable("menu.flightassistant")) {
    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, centerY - 40, this.width, this.font.lineHeight, this.title, this.font))

        /*this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.flight_plan")) {
            this.minecraft!!.setScreen(FlightPlanScreen())
        }.pos(this.centerX - 175, this.centerY - 10).width(100).build())*/ // TODO
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.autoflight")) {
            this.minecraft!!.setScreen(AutoFlightScreen())
        }.pos(this.centerX - 50, this.centerY + 30).width(100).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system")) {
            this.minecraft!!.setScreen(SystemStatusScreen())
        }.pos(this.centerX + 75, this.centerY - 10).width(100).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.config")) {
            this.minecraft!!.setScreen(FAConfigScreen.generate(null))
        }.pos(20, this.height - 40).width(100).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL) { _: Button? ->
            this.onClose()
        }.pos(this.width - 100, this.height - 40).width(80).build())
    }
}
