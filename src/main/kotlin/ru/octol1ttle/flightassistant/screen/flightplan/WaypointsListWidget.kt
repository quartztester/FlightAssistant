package ru.octol1ttle.flightassistant.screen.flightplan

import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import ru.octol1ttle.flightassistant.FlightAssistant.mc

class WaypointsListWidget(width: Int, height: Int, top: Int, @Suppress("UNUSED_PARAMETER", "KotlinRedundantDiagnosticSuppress") bottom: Int, left: Int)
    : ElementListWidget<WaypointsListWidget.AbstractWaypointEntry>(mc, width, height, top,
    /*? if <1.21 {*/ bottom, //?}
    40) {
    init {
//? if <1.21 {
        setRenderBackground(false)
        setRenderHorizontalShadows(false)
//?}

        var y: Int = top + OFFSET
        var id = 0
    }

//? if >=1.21 {
    /*override fun getScrollbarX(): Int {
        return this.x + this.width - 4
    }
*///?} else {
    override fun getScrollbarPositionX(): Int {
        return this.left + this.width - 4
    }
//?}

    override fun getRowWidth(): Int {
        return this.width
    }

    override fun isSelectedEntry(index: Int): Boolean {
        return this.selectedOrNull == children()[index]
    }

    abstract class AbstractWaypointEntry(val id: Int, val x: Int, val y: Int, val width: Int, val height: Int) : Entry<AbstractWaypointEntry>() {
        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            return !super.mouseClicked(mouseX, mouseY, button)
        }
    }

    companion object {
        const val OFFSET: Int = 5
        const val ITEM_HEIGHT: Int = 40
        val textFieldStorage: MutableMap<Int, MutableList<TextFieldWidget>> = HashMap()
    }
}
