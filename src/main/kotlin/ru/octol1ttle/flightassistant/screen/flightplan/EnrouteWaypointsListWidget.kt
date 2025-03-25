package ru.octol1ttle.flightassistant.screen.flightplan

import java.lang.IllegalStateException
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer

class EnrouteWaypointsListWidget(private val computers: ComputerView, width: Int, height: Int, top: Int, @Suppress("UNUSED_PARAMETER", "KotlinRedundantDiagnosticSuppress") bottom: Int, left: Int)
    : ElementListWidget<EnrouteWaypointsListWidget.WaypointEntry>(mc, width, height, top,
    /*? if <1.21 {*/ bottom, //?}
    40), FlightPlanState {
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

    class WaypointEntry(private val waypoint: FlightPlanComputer.EnrouteWaypoint, val x: Int, val y: Int, val width: Int, val height: Int) : Entry<WaypointEntry>(), FlightPlanState {
        override fun render(context: DrawContext?, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            TODO("Not yet implemented")
        }

        override fun children(): MutableList<out Element> {
            TODO("Not yet implemented")
        }

        override fun selectableChildren(): MutableList<out Selectable> {
            TODO("Not yet implemented")
        }

        override fun load() {
            throw IllegalStateException()
        }

        override fun needsSaving(): Boolean {
            TODO("Not yet implemented")
        }

        override fun canSave(): Boolean {
            TODO("Not yet implemented")
        }

        override fun save() {
            TODO("Not yet implemented")
        }
    }

    override fun load() {
        val waypoints: List<FlightPlanComputer.EnrouteWaypoint> = computers.plan.enrouteWaypoints ?: return
    }

    override fun needsSaving(): Boolean {
        return this.children().any { it.needsSaving() }
    }

    override fun canSave(): Boolean {
        return this.children().all { it.canSave() }
    }

    override fun save() {
        for (entry: WaypointEntry in this.children()) {
            entry.save()
        }
    }

    companion object {
        const val OFFSET: Int = 5
        const val ITEM_HEIGHT: Int = 40
        val textFields: MutableMap<Int, MutableList<TextFieldWidget>> = HashMap()
    }
}
