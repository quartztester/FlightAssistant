package ru.octol1ttle.flightassistant.screen

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.config.FAConfigScreen
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.autoflight.AutoFlightScreen
import ru.octol1ttle.flightassistant.screen.fms.departure.DepartureScreen
import ru.octol1ttle.flightassistant.screen.fms.enroute.EnrouteScreen
import ru.octol1ttle.flightassistant.screen.system.SystemManagementScreen

class FlightAssistantSetupScreen : FABaseScreen(Component.translatable("menu.flightassistant")) {
    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, 15, this.width, this.font.lineHeight, this.title, this.font))

        this.addRenderableWidget(StringWidget(0, this.centerY - 80, this.width, this.font.lineHeight, Component.translatable("menu.flightassistant.system"), this.font))
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.manage_displays")) {
            this.minecraft!!.setScreen(SystemManagementScreen(
                Component.translatable("menu.flightassistant.system.manage_displays"), "menu.flightassistant.system.name.hud", HudDisplayHost)
            )
        }.pos(this.centerX - 105, this.centerY - 65).width(100).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.system.manage_computers")) {
            this.minecraft!!.setScreen(SystemManagementScreen(
                Component.translatable("menu.flightassistant.system.manage_computers"), "menu.flightassistant.system.name.computer", ComputerHost)
            )
        }.pos(this.centerX + 5, this.centerY - 65).width(100).build())

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.autoflight")) {
            this.minecraft!!.setScreen(AutoFlightScreen())
        }.pos(this.centerX - 75, this.centerY - 30).width(150).build())

        this.addRenderableWidget(StringWidget(0, this.centerY + 5, this.width, this.font.lineHeight, Component.translatable("menu.flightassistant.fms"), this.font))
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.departure")) {
            this.minecraft!!.setScreen(DepartureScreen())
        }.pos(this.centerX - 160, this.centerY + 20).width(100).build())
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.enroute")) {
            this.minecraft!!.setScreen(EnrouteScreen())
        }.pos(this.centerX - 50, this.centerY + 20).width(100).build()).active = false
        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.arrival")) {
            //this.minecraft!!.setScreen(FlightPlanScreen())
        }.pos(this.centerX + 60, this.centerY + 20).width(100).build()).active = false

        this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.config")) {
            this.minecraft!!.setScreen(FAConfigScreen.generate(null))
        }.pos(10, this.height - 30).width(100).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }
}
