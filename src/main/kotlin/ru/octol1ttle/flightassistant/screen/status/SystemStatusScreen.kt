package ru.octol1ttle.flightassistant.screen.status

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class SystemStatusScreen : FABaseScreen(Component.translatable("menu.flightassistant.system")) {
    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, 7, this.width, 9, this.title, this.font))

        val top = 20
        val bottom: Int = this.height - 40
        val height: Int = bottom - top

        val hudListWidget = SystemStatusList(centerX, height, top, bottom, 0, HudDisplayHost, "menu.flightassistant.system.name.hud")
        this.addRenderableWidget(hudListWidget)

        val computerListWidget = SystemStatusList(centerX, height, top, bottom, this.centerX, ComputerHost, "menu.flightassistant.system.name.computer")
//? if >=1.21 {
        /*computerListWidget.x = this.centerX
*///?} else
        computerListWidget.setLeftPos(this.centerX)
        this.addRenderableWidget(computerListWidget)

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }
}
