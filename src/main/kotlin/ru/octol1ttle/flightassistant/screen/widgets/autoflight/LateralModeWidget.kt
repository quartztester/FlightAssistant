package ru.octol1ttle.flightassistant.screen.widgets.autoflight

import java.util.EnumMap
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.clearAndAdd
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer
import ru.octol1ttle.flightassistant.screen.widgets.AbstractParentWidget

class LateralModeWidget(val computers: ComputerView, val x: Int, val y: Int, val width: Int) : AbstractParentWidget(), DelayedApplyChanges {
    private val title: TextWidget = TextWidget(
        x, y, width, 20, Text.translatable("menu.flightassistant.autoflight.lateral"), mc.textRenderer
    )
    private var newType: AutopilotLogicComputer.LateralMode.Type = computers.autopilot.lateralMode.type

    init {
        initSelectedHeading()
        initSelectedCoordinates()

        buttons[AutopilotLogicComputer.LateralMode.Type.WaypointCoordinates] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.lateral.waypoint_coordinates")
        ) { newType = AutopilotLogicComputer.LateralMode.Type.WaypointCoordinates }
            .dimensions(x + (width * (2 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()
    }

    private fun initSelectedHeading() {
        val type = AutopilotLogicComputer.LateralMode.Type.SelectedHeading

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.lateral.selected_heading")
        ) { newType = type }
            .dimensions(x + 1, y + 20, width / 3 - 1, 15).build()
        val targetHeadingWidget = TextFieldWidget(
            mc.textRenderer, x + width / 4, y + 40, width / 2, 15, textFields[type]?.singleOrNull(), Text.empty()
        )
        targetHeadingWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.lateral.selected_heading.target"))
        targetHeadingWidget.setTextPredicate {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i in 0..360
        }
        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(targetHeadingWidget)
    }

    private fun initSelectedCoordinates() {
        val type = AutopilotLogicComputer.LateralMode.Type.SelectedCoordinates

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.lateral.selected_coordinates")
        ) { newType = type }
            .dimensions(x + (width * (1 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()

        val xCoordWidget = TextFieldWidget(
            mc.textRenderer, x + 2, y + 40, width / 2 - 4, 15, textFields[type]?.firstOrNull(), Text.empty()
        )
        xCoordWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.lateral.selected_coordinates.target_x"))
        xCoordWidget.setTextPredicate {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }

        val zCoordWidget = TextFieldWidget(
            mc.textRenderer, x + width / 2 + 3, y + 40, width / 2 - 4, 15, textFields[type]?.get(1), Text.empty()
        )
        zCoordWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.lateral.selected_coordinates.target_z"))
        zCoordWidget.setTextPredicate {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }

        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(xCoordWidget, zCoordWidget)
    }

    override fun children(): MutableList<out Element> {
        val list = ArrayList<Element>()
        list.add(title)
        list.addAll(buttons.values)
        textFields[newType]?.let {
            list.addAll(it)
        }
        return list
    }

    override fun applyChanges() {
        computers.autopilot.lateralMode.type = newType
        when (val type: AutopilotLogicComputer.LateralMode.Type = computers.autopilot.lateralMode.type) {
            AutopilotLogicComputer.LateralMode.Type.SelectedHeading -> computers.autopilot.lateralMode.heading = textFields[type]!!.single().text.toFloatOrNull() ?: 0.0f
            AutopilotLogicComputer.LateralMode.Type.SelectedCoordinates -> {
                computers.autopilot.lateralMode.x = textFields[type]!!.first().text.toDoubleOrNull() ?: 0.0
                computers.autopilot.lateralMode.z = textFields[type]!![1].text.toDoubleOrNull() ?: 0.0
            }
            else -> Unit
        }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        for (button in buttons) {
            button.value.active = newType != button.key
        }

        super.render(context, mouseX, mouseY, delta)
    }

    companion object {
        private val buttons: EnumMap<AutopilotLogicComputer.LateralMode.Type, ButtonWidget> = EnumMap(AutopilotLogicComputer.LateralMode.Type::class.java)
        private val textFields: EnumMap<AutopilotLogicComputer.LateralMode.Type, MutableList<TextFieldWidget>> = EnumMap(AutopilotLogicComputer.LateralMode.Type::class.java)
        const val TOTAL_MODES: Float = 3.0f
    }
}
