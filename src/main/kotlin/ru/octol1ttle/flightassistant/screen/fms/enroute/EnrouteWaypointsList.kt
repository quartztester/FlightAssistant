package ru.octol1ttle.flightassistant.screen.fms.enroute

import kotlin.random.Random
import kotlin.random.nextInt
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.drawString
import ru.octol1ttle.flightassistant.api.util.extensions.font
import ru.octol1ttle.flightassistant.api.util.extensions.toIntOrNullWithFallback
import ru.octol1ttle.flightassistant.screen.components.FABaseList
import ru.octol1ttle.flightassistant.screen.components.TypeStrictEditBox

class EnrouteWaypointsList(y0: Int, y1: Int, width: Int, columns: Float) : FABaseList<EnrouteWaypointsList.Entry>(y0, y1, width, ITEM_HEIGHT) {
    init {
        repeat(Random.nextInt(1..69)) {
            addEntry(Entry(width, columns, EnrouteScreenState.Waypoint(Random.nextInt(-30_000_000..30_000_000), Random.nextInt(-30_000_000..30_000_000), Random.nextInt(62..620), Random.nextInt(0..420))))
        }
    }

    class Entry(val width: Int, val columns: Float, val state: EnrouteScreenState.Waypoint) : ContainerObjectSelectionList.Entry<Entry>() {
        private val columnWidth: Float = width / this.columns

        private val xEditBox = TypeStrictEditBox(0, 0, columnWidth.toInt(), font.lineHeight, state.coordinatesX, { state.coordinatesX = it }, String::toIntOrNullWithFallback)
        private val zEditBox = TypeStrictEditBox(0, 0, columnWidth.toInt(), font.lineHeight, state.coordinatesZ, { state.coordinatesZ = it }, String::toIntOrNullWithFallback)
        private val altitudeEditBox = TypeStrictEditBox(0, 0, columnWidth.toInt(), font.lineHeight, state.altitude, { state.altitude = it }, String::toIntOrNullWithFallback)
        private val speedEditBox = TypeStrictEditBox(0, 0, columnWidth.toInt(), font.lineHeight, state.speed, { state.speed = it }, String::toIntOrNullWithFallback) { it >= 0 }

        private val moveUpButton = Button.builder(Component.literal("↑")) {}.size(12, 12).build()
        private val moveDownButton = Button.builder(Component.literal("↓")) {}.size(12, 12).build()
        private val deleteButton = Button.builder(Component.literal("X")) {}.size(12, 12).build()

        private var hovering: Boolean = false
        val children = listOf(xEditBox, zEditBox, altitudeEditBox, speedEditBox)
        val childrenWhenHovering = listOf(xEditBox, zEditBox, altitudeEditBox, speedEditBox, moveUpButton, moveDownButton, deleteButton)

        override fun render(guiGraphics: GuiGraphics, index: Int, top: Int, left: Int, width: Int, height: Int, mouseX: Int, mouseY: Int, hovering: Boolean, partialTick: Float) {
            this.hovering = hovering

            guiGraphics.drawString((index + 1).toString(), (width * (0.4f / this.columns)).toInt(), top, ChatFormatting.WHITE.color!!)

            children().filterIsInstance<TypeStrictEditBox<*>>().forEachIndexed { i, editBox ->
                @Suppress("UsePropertyAccessSyntax")
                editBox.setBordered(false)
                editBox.x = (columnWidth * (i + 1)).toInt()
                editBox.y = top
                editBox.render(guiGraphics, mouseX, mouseY, partialTick)
            }

            children().filterIsInstance<Button>().forEachIndexed { i, button ->
                button.x = (columnWidth * (if (columns <= 7) columns - 1 else 7.0f) + i * 15).toInt()
                button.y = top - button.height / 4
                button.render(guiGraphics, mouseX, mouseY, partialTick)
            }
        }

        override fun children(): List<GuiEventListener> {
            return if (this.hovering) childrenWhenHovering else children
        }

        override fun narratables(): List<NarratableEntry> {
            return if (this.hovering) childrenWhenHovering else children
        }
    }

    companion object {
        private const val ITEM_HEIGHT: Int = 10
    }
}