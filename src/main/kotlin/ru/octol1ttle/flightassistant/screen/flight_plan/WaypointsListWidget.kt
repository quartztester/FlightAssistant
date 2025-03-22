package ru.octol1ttle.flightassistant.screen.flight_plan

import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.util.extensions.textRenderer

class WaypointsListWidget(width: Int, height: Int, top: Int, @Suppress("UNUSED_PARAMETER", "KotlinRedundantDiagnosticSuppress") bottom: Int, left: Int)
    : ElementListWidget<WaypointsListWidget.AbstractWaypointEntry>(mc, width, height, top,
    /*? if <1.21 {*/ bottom, //?}
    40) {
    init {
        setRenderBackground(false)
        setRenderHorizontalShadows(false)

        var y: Int = top + OFFSET
        var id = 0

        textFieldStorage[id] = ArrayList()
        this.addEntryToTop(DepartureWaypointEntry(id, left + OFFSET, y, width - OFFSET, 40))
        id++
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

    class DepartureWaypointEntry(id: Int, x: Int, y: Int, width: Int, height: Int) : AbstractWaypointEntry(id, x, y, width, height) {
        private val displayName: TextWidget = TextWidget(x, y, width, 9, Text.translatable("menu.flightassistant.flight_plan.departure_waypoint"), textRenderer).alignLeft()
        private val xField: TextFieldWidget = TextFieldWidget(
            mc.textRenderer, x, y + 11, width / 2 - 4, 15, textFieldStorage[id]?.getOrNull(0), Text.empty()
        )
        private val zField: TextFieldWidget = TextFieldWidget(
            mc.textRenderer, x + width / 2, y + 11, width / 2 - 4, 15, textFieldStorage[id]?.getOrNull(0), Text.empty()
        )

        init {
            textFieldStorage[id]!!.add(0, xField)
            textFieldStorage[id]!!.add(1, zField)
        }

        override fun children(): List<Element> {
            return ImmutableList.of(displayName, xField, zField)
        }

        override fun selectableChildren(): MutableList<out Selectable> {
            return ImmutableList.of(displayName, xField, zField)
        }

        override fun render(context: DrawContext, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            val renderY: Int = y + OFFSET

            displayName.y = renderY
            displayName.render(context, mouseX, mouseY, tickDelta)

            xField.y = renderY + 11
            xField.render(context, mouseX, mouseY, tickDelta)
            zField.y = renderY + 11
            zField.render(context, mouseX, mouseY, tickDelta)
        }
    }

    companion object {
        const val OFFSET: Int = 5
        val textFieldStorage: MutableMap<Int, MutableList<TextFieldWidget>> = HashMap()
    }
}
