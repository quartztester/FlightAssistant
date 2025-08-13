package ru.octol1ttle.flightassistant.screen.fms.enroute

import kotlin.random.Random
import kotlin.random.nextInt
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.screen.components.FABaseList

class EnrouteWaypointsList(xOffset: Int, y0: Int, y1: Int, width: Int, columns: Float) : FABaseList<EnrouteWaypointsList.Entry>(y0, y1, width, ITEM_HEIGHT) {
    init {
        for (i: Int in 0..Random.nextInt(1..69)) {
            addEntry(Entry(xOffset, columns, EnrouteScreenState.Waypoint(Random.nextInt(-30_000_000..30_000_000), Random.nextInt(-30_000_000..30_000_000), Random.nextInt(62..620), Random.nextInt(0..420))))
        }
    }

    class Entry(val xOffset: Int, val columns: Float, val state: EnrouteScreenState.Waypoint) : ContainerObjectSelectionList.Entry<Entry>() {
        override fun render(guiGraphics: GuiGraphics, index: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hovering: Boolean, partialTick: Float) {
            // TODO: text is cool but we need edit boxes!! SeamlessEditBox
            guiGraphics.drawString((index + 1).toString(), xOffset + (width * (0.4f / this.columns)).toInt(), top, ChatFormatting.WHITE.color!!)
            guiGraphics.drawString(state.coordinatesX.toString(), xOffset + (width * (1 / this.columns)).toInt(), top, ChatFormatting.WHITE.color!!)
            guiGraphics.drawString(state.coordinatesZ.toString(), xOffset + (width * (2 / this.columns)).toInt(), top, ChatFormatting.WHITE.color!!)
            guiGraphics.drawString(state.altitude.toString(), xOffset + (width * (3 / this.columns)).toInt(), top, ChatFormatting.WHITE.color!!)
            guiGraphics.drawString(state.speed.toString(), xOffset + (width * (4 / this.columns)).toInt(), top, ChatFormatting.WHITE.color!!)
        }

        override fun children(): List<GuiEventListener> {
            return emptyList()
        }

        override fun narratables(): List<NarratableEntry> {
            return emptyList()
        }
    }

    companion object {
        private const val ITEM_HEIGHT: Int = 10
    }
}