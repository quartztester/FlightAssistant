package ru.octol1ttle.flightassistant.screen.fms.enroute

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import ru.octol1ttle.flightassistant.screen.components.FABaseList

class EnrouteWaypointsList(width: Int, height: Int, y0: Int, y1: Int)
    : FABaseList<EnrouteWaypointsList.Entry>(width, height, y0, y1, ITEM_HEIGHT) {
    init {
    }

    class Entry(val x: Int, val y: Int, val state: EnrouteScreenState.EnrouteWaypointState) : ContainerObjectSelectionList.Entry<Entry>() {
        override fun render(guiGraphics: GuiGraphics, index: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hovering: Boolean, partialTick: Float) {
            TODO("Not yet implemented")
        }

        override fun children(): List<GuiEventListener> {
            TODO("Not yet implemented")
        }

        override fun narratables(): List<NarratableEntry> {
            TODO("Not yet implemented")
        }
    }

    companion object {
        private const val ITEM_HEIGHT: Int = 20
    }
}