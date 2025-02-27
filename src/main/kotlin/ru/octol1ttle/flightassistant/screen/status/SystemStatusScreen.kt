package ru.octol1ttle.flightassistant.screen.status

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.impl.display.HudDisplayHost
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class SystemStatusScreen : FABaseScreen(Text.translatable("menu.flightassistant.system")) {
    override fun init() {
        super.init()

        this.addDrawableChild(TextWidget(0, 7, this.width, 9, this.title, this.textRenderer))

        val hudListWidget = SystemStatusListWidget(centerX, this.height, 20, this.height - 40, 0, HudDisplayHost, "menu.flightassistant.system.name.hud")
        this.addDrawableChild(hudListWidget)

        val computerListWidget = SystemStatusListWidget(centerX, this.height, 20, this.height - 40, this.centerX, ComputerHost, "menu.flightassistant.system.name.computer")
//? if >=1.21 {
        /*computerListWidget.x = this.centerX
*///?} else
        computerListWidget.setLeftPos(this.centerX)
        this.addDrawableChild(computerListWidget)

        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) { _: ButtonWidget? ->
            this.close()
        }.position(this.width - 90, this.height - 30).width(80).build())
    }
}
