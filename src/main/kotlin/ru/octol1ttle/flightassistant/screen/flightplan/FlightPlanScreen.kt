package ru.octol1ttle.flightassistant.screen.flightplan

import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class FlightPlanScreen : FABaseScreen(Text.translatable("menu.flightassistant.flight_plan")) {
    override fun init() {
        super.init()

        this.addDrawableChild(TextWidget(0, 10, this.width, 9, this.title, this.textRenderer))

        val width: Int = this.width / 3 - WaypointsListWidget.OFFSET
        val top: Int = WaypointsListWidget.ITEM_HEIGHT + 10
        val left: Int = WaypointsListWidget.OFFSET
        val bottom: Int = this.height - WaypointsListWidget.ITEM_HEIGHT - 10
        val height: Int = bottom - top

        val departureWaypointWidget = DepartureWaypointWidget(left, 5, width, WaypointsListWidget.ITEM_HEIGHT)
        val arrivalWaypointWidget = ArrivalWaypointWidget(left, this.height - WaypointsListWidget.ITEM_HEIGHT - 5, width, WaypointsListWidget.ITEM_HEIGHT)
        val waypointsListWidget = WaypointsListWidget(width, height, top, bottom, 5)
//? if >=1.21 {
        /*waypointsListWidget.x = left
*///?} else
        waypointsListWidget.setLeftPos(left)

        this.addDrawableChild(departureWaypointWidget)
        this.addDrawableChild(arrivalWaypointWidget)
        this.addDrawableChild(waypointsListWidget)
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.DONE) { _: ButtonWidget? ->
            this.close()
        }.position(this.width - 100, this.height - 40).width(80).build())
    }
}
