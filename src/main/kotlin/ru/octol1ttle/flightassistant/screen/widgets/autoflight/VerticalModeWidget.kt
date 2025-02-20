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

class VerticalModeWidget(val computers: ComputerView, val x: Int, val y: Int, val width: Int) : AbstractParentWidget(), DelayedApplyChanges {
    private val title: TextWidget = TextWidget(
        x, y, width, 20, Text.translatable("menu.flightassistant.autoflight.vertical"), mc.textRenderer
    )
    private var newType: AutopilotLogicComputer.VerticalMode.Type = computers.autopilot.verticalMode.type

    init {
        initSelectedPitch()
        initSelectedAltitude()

        buttons[AutopilotLogicComputer.VerticalMode.Type.WaypointAltitude] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.vertical.waypoint_altitude")
        ) { newType = AutopilotLogicComputer.VerticalMode.Type.WaypointAltitude }
            .dimensions(x + (width * (2 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()
    }

    private fun initSelectedPitch() {
        val type = AutopilotLogicComputer.VerticalMode.Type.SelectedPitch

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.vertical.selected_pitch")
        ) { newType = type }
            .dimensions(x + 1, y + 20, width / 3 - 1, 15).build()
        val selectedPitchWidget = TextFieldWidget(
            mc.textRenderer, x + width / 4, y + 40, width / 2, 15, textFields[type]?.singleOrNull(), Text.empty()
        )
        selectedPitchWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.vertical.selected_pitch.target"))
        selectedPitchWidget.setTextPredicate {
            val i: Float? = it.toFloatOrNull()
            it.isEmpty() || it == "-" || i != null && i in -90.0f..90.0f
        }
        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(selectedPitchWidget)
    }

    private fun initSelectedAltitude() {
        val type = AutopilotLogicComputer.VerticalMode.Type.SelectedAltitude

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.vertical.selected_altitude")
        ) { newType = type }
            .dimensions(x + (width * (1 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()

        val selectedAltitudeWidget = TextFieldWidget(
            mc.textRenderer, x + width / 4, y + 40, width / 2, 15, textFields[type]?.singleOrNull(), Text.empty()
        )
        selectedAltitudeWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.vertical.selected_altitude.target"))
        selectedAltitudeWidget.setTextPredicate {
            it.isEmpty() || it == "-" || it.toIntOrNull() != null
        }
        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(selectedAltitudeWidget)
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
        computers.autopilot.verticalMode.type = newType
        when (val type: AutopilotLogicComputer.VerticalMode.Type = computers.autopilot.verticalMode.type) {
            AutopilotLogicComputer.VerticalMode.Type.SelectedPitch, AutopilotLogicComputer.VerticalMode.Type.SelectedAltitude
                 -> computers.autopilot.verticalMode.pitchOrAltitude = textFields[type]!!.single().text.toFloatOrNull() ?: 0.0f
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
        private val buttons: EnumMap<AutopilotLogicComputer.VerticalMode.Type, ButtonWidget> = EnumMap(AutopilotLogicComputer.VerticalMode.Type::class.java)
        private val textFields: EnumMap<AutopilotLogicComputer.VerticalMode.Type, MutableList<TextFieldWidget>> = EnumMap(AutopilotLogicComputer.VerticalMode.Type::class.java)
        const val TOTAL_MODES: Float = 3.0f
    }
}
