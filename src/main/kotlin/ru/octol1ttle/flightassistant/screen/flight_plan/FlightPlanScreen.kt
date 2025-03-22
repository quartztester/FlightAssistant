package ru.octol1ttle.flightassistant.screen.flight_plan

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class FlightPlanScreen : FABaseScreen(Text.translatable("menu.flightassistant.flight_plan")) {
    override fun init() {
        super.init()

        this.addDrawableChild(TextWidget(0, 10, this.width, 9, this.title, this.textRenderer))

        val waypointsListWidget = WaypointsListWidget(this.width / 3 - 5, this.height, 20, this.height - 40, 5)
//? if >=1.21 {
        /*waypointsListWidget.x = 5
*///?} else
        waypointsListWidget.setLeftPos(5)
        this.addDrawableChild(waypointsListWidget)
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) { _: ButtonWidget? ->
            this.close()
        }.position(this.width - 100, this.height - 40).width(80).build())
    }
}
