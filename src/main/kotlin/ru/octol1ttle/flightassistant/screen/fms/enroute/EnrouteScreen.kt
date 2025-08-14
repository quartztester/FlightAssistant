package ru.octol1ttle.flightassistant.screen.fms.enroute

import kotlin.math.max
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.literal
import net.minecraft.network.chat.Component.translatable
import ru.octol1ttle.flightassistant.screen.FABaseScreen
import ru.octol1ttle.flightassistant.screen.components.SmartStringWidget

class EnrouteScreen(parent: Screen) : FABaseScreen(parent, Component.translatable("menu.flightassistant.fms.enroute")) {
    private lateinit var discardChanges: Button

    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, 7, this.width, 9, this.title, this.font))

        val columnsSizeWithMargin: Float = COLUMNS.size + HOVERING_COLUMNS_MARGIN
        val optimumColumnsSize: Float = (if (this.width / columnsSizeWithMargin >= 75) columnsSizeWithMargin else COLUMNS.size.toFloat())
        COLUMNS.forEachIndexed { i, component ->
            this.addRenderableWidget(SmartStringWidget((this.width * (max(0.4f, i.toFloat()) / optimumColumnsSize)).toInt(), Y0, component).setColor(ChatFormatting.GRAY.color!!))
        }

        this.addRenderableWidget(EnrouteWaypointsList(0, Y0 + 10, this.height - Y0 * 2, this.width, optimumColumnsSize))

        discardChanges = this.addRenderableWidget(Button.builder(Component.translatable("menu.flightassistant.fms.discard_changes")) { _: Button? ->
            state = lastState.copy()
            this.rebuildWidgets()
        }.pos(this.width - 200, this.height - 30).width(100).build())

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }

    override fun onClose() {
        lastState = state.copy()
        state.save(computers.plan)
        super.onClose()
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        discardChanges.active = !state.equals(lastState)

        super.render(guiGraphics, mouseX, mouseY, delta)
    }

    companion object {
        private const val Y0: Int = 30

        private val COLUMNS: Array<Component> = arrayOf(literal("#"), literal("X"), literal("Z"), translatable("short.flightassistant.altitude"), translatable("short.flightassistant.speed"), translatable("short.flightassistant.distance"), translatable("short.flightassistant.time"))
        private const val HOVERING_COLUMNS_MARGIN: Float = 1.5f

        private var lastState: EnrouteScreenState = EnrouteScreenState()
        private var state: EnrouteScreenState = EnrouteScreenState()
    }
}