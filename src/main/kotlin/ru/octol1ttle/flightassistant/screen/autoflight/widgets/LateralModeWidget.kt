package ru.octol1ttle.flightassistant.screen.autoflight.widgets

import java.util.EnumMap
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.clearAndAdd
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer
import ru.octol1ttle.flightassistant.screen.AbstractParentWidget

class LateralModeWidget(val computers: ComputerView, val x: Int, val y: Int, val width: Int) : AbstractParentWidget(), DelayedApplyChanges {
    private val title: StringWidget = StringWidget(
        x, y, width, 20, Component.translatable("menu.flightassistant.autoflight.lateral"), mc.font
    )
    private var newType: ButtonType

    init {
        newType = ButtonType.entries.single { it.matches(computers.autopilot.lateralMode) }

        initSelectedHeading()
        initSelectedCoordinates()

        buttons[ButtonType.FlightPlan] = Button.builder(
            Component.translatable("menu.flightassistant.autoflight.lateral.waypoint_coordinates")
        ) { newType = ButtonType.FlightPlan }
            .bounds(x + (width * (2 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()
    }

    private fun initSelectedHeading() {
        val type = ButtonType.SelectedHeading

        buttons[type] = Button.builder(
            Component.translatable("menu.flightassistant.autoflight.lateral.selected_heading")
        ) { newType = type }
            .bounds(x + 1, y + 20, width / 3 - 1, 15).build()
        val targetHeadingWidget = EditBox(
            mc.font, x + width / 4, y + 40, width / 2, 15, editBoxes[type]?.singleOrNull(), Component.empty()
        )
        targetHeadingWidget.setHint(Component.translatable("menu.flightassistant.autoflight.lateral.selected_heading.target"))
        targetHeadingWidget.setFilter {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i in 0..360
        }
        editBoxes.computeIfAbsent(type) { ArrayList() }.clearAndAdd(targetHeadingWidget)
    }

    private fun initSelectedCoordinates() {
        val type = ButtonType.SelectedCoordinates

        buttons[type] = Button.builder(
            Component.translatable("menu.flightassistant.autoflight.lateral.selected_coordinates")
        ) { newType = type }
            .bounds(x + (width * (1 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()

        val xCoordWidget = EditBox(
            mc.font, x + 2, y + 40, width / 2 - 4, 15, editBoxes[type]?.getOrNull(0), Component.empty()
        )
        xCoordWidget.setHint(Component.translatable("menu.flightassistant.autoflight.lateral.target_x"))
        xCoordWidget.setFilter {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }

        val zCoordWidget = EditBox(
            mc.font, x + width / 2 + 3, y + 40, width / 2 - 4, 15, editBoxes[type]?.getOrNull(1), Component.empty()
        )
        zCoordWidget.setHint(Component.translatable("menu.flightassistant.autoflight.lateral.target_z"))
        zCoordWidget.setFilter {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }

        editBoxes.computeIfAbsent(type) { ArrayList() }.clearAndAdd(xCoordWidget, zCoordWidget)
    }


    override fun children(): MutableList<out Element> {
        val list = ArrayList<Element>()
        list.add(title)
        list.addAll(buttons.values)
        editBoxes[newType]?.let {
            list.addAll(it)
        }
        return list
    }

    override fun applyChanges() {
        computers.autopilot.lateralMode = when (val type: ButtonType = newType) {
            ButtonType.SelectedHeading -> {
                val heading: Float? = editBoxes[type]!!.single().value.toFloatOrNull()
                if (heading != null) AutopilotLogicComputer.HeadingLateralMode(heading) else computers.autopilot.lateralMode
            }
            ButtonType.SelectedCoordinates -> {
                val x: Double? = editBoxes[type]!![0].value.toDoubleOrNull()
                val z: Double? = editBoxes[type]!![1].value.toDoubleOrNull()
                if (x != null && z != null) AutopilotLogicComputer.CoordinatesLateralMode(x, z) else computers.autopilot.lateralMode
            }
            ButtonType.FlightPlan -> null
        }
    }

    override fun render(context: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float) {
        for (button in buttons) {
            button.value.active = newType != button.key
        }

        super.render(context, mouseX, mouseY, delta)
    }

    enum class ButtonType {
        SelectedHeading {
            override fun matches(mode: AutopilotLogicComputer.LateralMode?): Boolean {
                return mode is AutopilotLogicComputer.HeadingLateralMode
            }
        },
        SelectedCoordinates {
            override fun matches(mode: AutopilotLogicComputer.LateralMode?): Boolean {
                return mode is AutopilotLogicComputer.CoordinatesLateralMode
            }
        },
        FlightPlan {
            override fun matches(mode: AutopilotLogicComputer.LateralMode?): Boolean {
                return mode == null
            }
        };

        abstract fun matches(mode: AutopilotLogicComputer.LateralMode?): Boolean
    }

    companion object {
        private val buttons: EnumMap<ButtonType, Button> = EnumMap(ButtonType::class.java)
        private val editBoxes: EnumMap<ButtonType, MutableList<EditBox>> = EnumMap(ButtonType::class.java)
        const val TOTAL_MODES: Float = 3.0f
    }
}
