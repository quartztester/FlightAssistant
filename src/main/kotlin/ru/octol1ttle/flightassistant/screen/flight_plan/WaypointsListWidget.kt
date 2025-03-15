package ru.octol1ttle.flightassistant.screen.flight_plan

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc

class WaypointsListWidget(width: Int, height: Int, top: Int, @Suppress("UNUSED_PARAMETER", "KotlinRedundantDiagnosticSuppress") bottom: Int, left: Int)
    : AlwaysSelectedEntryListWidget<WaypointsListWidget.WaypointsListEntry>(mc, width, height, top,
    /*? if <1.21 {*/ bottom, //?}
    25) {
    init {

    }

    class WaypointsListEntry : Entry<WaypointsListEntry>() {
        override fun render(context: DrawContext?, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            TODO("Not yet implemented")
        }

        override fun getNarration(): Text {
            return Text.empty()
        }
    }
}
