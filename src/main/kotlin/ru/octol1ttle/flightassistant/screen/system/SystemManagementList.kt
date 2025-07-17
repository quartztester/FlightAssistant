package ru.octol1ttle.flightassistant.screen.system

import com.google.common.collect.ImmutableList
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.ContainerObjectSelectionList
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.components.events.GuiEventListener
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.ModuleController
import ru.octol1ttle.flightassistant.api.util.extensions.cautionColor
import ru.octol1ttle.flightassistant.api.util.extensions.font

class SystemManagementList(width: Int, height: Int, top: Int, @Suppress("UNUSED_PARAMETER", "KotlinRedundantDiagnosticSuppress") bottom: Int, left: Int, baseKey: String, controller: ModuleController<*>) : ContainerObjectSelectionList<SystemManagementList.Entry>(
    mc, width, height, top,
    /*? if <1.21 {*/ bottom, //?}
    25) {
    init {
//? if <1.21 {
        setRenderBackground(false)
        setRenderTopAndBottom(false)
//?}
        var y: Int = top + Y_OFFSET
        for (module: ResourceLocation in controller.identifiers()) {
            this.addEntry(
                Entry(
                    left, y, width, module, Component.translatable("$baseKey.$module"), controller
                )
            )
            y += 25
        }
    }

//? if >=1.21.4 {
    /*override fun scrollBarX(): Int {
*///?} else
    override fun getScrollbarPosition(): Int {
        return this.width - 6
    }

    override fun getRowWidth(): Int {
        return this.width
    }

    class Entry(val x: Int, val y: Int, private val listWidth: Int, private val identifier: ResourceLocation, displayName: Component, private val controller: ModuleController<*>) : ContainerObjectSelectionList.Entry<Entry>() {
        private val displayName: StringWidget = StringWidget(x, y, this.listWidth / 2, 9, displayName, font).alignLeft()
        private val faultText: StringWidget = StringWidget(x, y, this.listWidth / 6, 9, FAULT_TEXT, font)
        private val offText: StringWidget = StringWidget(x, y, this.listWidth / 6, 9, OFF_TEXT, font)
        private val toggleButton: Button = Button.builder(OFF_TEXT) {
            controller.toggleEnabled(identifier)
        }.pos(x, y).width(60).build()

        override fun render(context: GuiGraphics, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, partialTick: Float) {
            val renderY: Int = y + Y_OFFSET

            this@Entry.displayName.x = this.x + 10
            this@Entry.displayName.y = renderY
            this@Entry.displayName.render(context, mouseX, mouseY, partialTick)

            toggleButton.x = this.x + this.listWidth - toggleButton.width - 10
            toggleButton.y = renderY - toggleButton.height / 4 - 1
            toggleButton.message =
                if (controller.isEnabled(identifier))
                    if (controller.modulesResettable) OFF_RESET_TEXT else OFF_TEXT
                else ON_TEXT
            toggleButton.render(context, mouseX, mouseY, partialTick)

            offText.x = toggleButton.x - this.listWidth / 12 - font.width(OFF_TEXT)
            offText.y = renderY
            @Suppress("UsePropertyAccessSyntax")
            offText.setColor(if (controller.isEnabled(identifier)) ChatFormatting.DARK_GRAY.color!! else 0xFFFFFF)
            offText.render(context, mouseX, mouseY, partialTick)

            faultText.x = offText.x - font.width(FAULT_TEXT)
            faultText.y = renderY
            @Suppress("UsePropertyAccessSyntax")
            faultText.setColor(if (controller.isFaulted(identifier)) cautionColor else ChatFormatting.DARK_GRAY.color!!)
            faultText.render(context, mouseX, mouseY, partialTick)
        }

        override fun children(): MutableList<out GuiEventListener> {
            return ImmutableList.of(this@Entry.displayName, faultText, offText, toggleButton)
        }

        override fun narratables(): MutableList<out NarratableEntry> {
            return ImmutableList.of(this@Entry.displayName, faultText, offText, toggleButton)
        }

        companion object {
            val FAULT_TEXT: Component = Component.translatable("menu.flightassistant.system.fault")
            val OFF_TEXT: Component = Component.translatable("menu.flightassistant.system.off")
            val OFF_RESET_TEXT: Component = Component.translatable("menu.flightassistant.system.off_reset")
            val ON_TEXT: Component = Component.translatable("menu.flightassistant.system.on")
        }
    }

    companion object {
        const val Y_OFFSET: Int = 5
    }
}
