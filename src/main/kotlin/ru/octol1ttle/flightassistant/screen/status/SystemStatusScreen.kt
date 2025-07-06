package ru.octol1ttle.flightassistant.screen.status

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class SystemStatusScreen : FABaseScreen(Component.translatable("menu.flightassistant.system")) {
    override fun init() {
        super.init()

        this.addDrawableChild(TextWidget(0, 7, this.width, 9, this.title, this.textRenderer))

        val top = 20
        val bottom: Int = this.height - 40
        val height: Int = bottom - top

        val hudListWidget = SystemStatusListWidget(centerX, height, top, bottom, 0, HudDisplayHost, "menu.flightassistant.system.name.hud")
        this.addDrawableChild(hudListWidget)

        val computerListWidget = SystemStatusListWidget(centerX, height, top, bottom, this.centerX, ComputerHost, "menu.flightassistant.system.name.computer")
//? if >=1.21 {
        computerListWidget.x = this.centerX
//?} else
        /*computerListWidget.setLeftPos(this.centerX)*/
        this.addDrawableChild(computerListWidget)

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) { _: ButtonWidget? ->
            this.close()
        }.position(this.width - 90, this.height - 30).width(80).build())
    }
}
