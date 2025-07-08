package ru.octol1ttle.flightassistant.screen.autoflight.widgets

import java.util.EnumMap
import kotlin.collections.single
import kotlin.collections.singleOrNull
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.widget.Button
import net.minecraft.client.gui.widget.EditBox
import net.minecraft.client.gui.widget.StringWidget
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.clearAndAdd
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer
import ru.octol1ttle.flightassistant.screen.AbstractParentWidget

class VerticalModeWidget(val computers: ComputerView, val x: Int, val y: Int, val width: Int) : AbstractParentWidget(), DelayedApplyChanges {
    private val title: StringWidget = StringWidget(
        x, y, width, 20, Component.translatable("menu.flightassistant.autoflight.vertical"), mc.font
    )
    private var newType: ButtonType

    init {
        newType = ButtonType.entries.single { it.matches(computers.autopilot.verticalMode) }

        initSelectedPitch()
        initSelectedAltitude()

        buttons[ButtonType.FlightPlan] = Button.builder(
            Component.translatable("menu.flightassistant.autoflight.vertical.waypoint_altitude")
        ) { newType = ButtonType.FlightPlan }
            .bounds(x + (width * (2 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()
    }

    private fun initSelectedPitch() {
        val type = ButtonType.SelectedPitch

        buttons[type] = Button.builder(
            Component.translatable("menu.flightassistant.autoflight.vertical.selected_pitch")
        ) { newType = type }
            .bounds(x + 1, y + 20, width / 3 - 1, 15).build()
        val selectedPitchWidget = EditBox(
            mc.font, x + width / 4, y + 40, width / 2, 15, textFields[type]?.singleOrNull(), Component.empty()
        )
        selectedPitchWidget.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.vertical.selected_pitch.target"))
        selectedPitchWidget.setTextPredicate {
            val i: Float? = it.toFloatOrNull()
            it.isEmpty() || it == "-" || i != null && i in -90.0f..90.0f
        }
        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(selectedPitchWidget)
    }

    private fun initSelectedAltitude() {
        val type = ButtonType.SelectedAltitude

        buttons[type] = Button.builder(
            Component.translatable("menu.flightassistant.autoflight.vertical.selected_altitude")
        ) { newType = type }
            .bounds(x + (width * (1 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()

        val selectedAltitudeWidget = EditBox(
            mc.font, x + width / 4, y + 40, width / 2, 15, textFields[type]?.singleOrNull(), Component.empty()
        )
        selectedAltitudeWidget.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.vertical.target_altitude"))
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
        computers.autopilot.verticalMode = when (val type: ButtonType = newType) {
            ButtonType.SelectedPitch -> {
                val pitch: Float? = textFields[type]!!.single().text.toFloatOrNull()
                if (pitch != null) AutopilotLogicComputer.PitchVerticalMode(pitch) else computers.autopilot.verticalMode
            }
            ButtonType.SelectedAltitude -> {
                val altitude: Double? = textFields[type]!!.single().text.toDoubleOrNull()
                if (altitude != null) AutopilotLogicComputer.SelectedAltitudeVerticalMode(altitude) else computers.autopilot.verticalMode
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
        SelectedPitch {
            override fun matches(mode: AutopilotLogicComputer.VerticalMode?): Boolean {
                return mode is AutopilotLogicComputer.PitchVerticalMode
            }
        },
        SelectedAltitude {
            override fun matches(mode: AutopilotLogicComputer.VerticalMode?): Boolean {
                return mode is AutopilotLogicComputer.SelectedAltitudeVerticalMode
            }
        },
        FlightPlan {
            override fun matches(mode: AutopilotLogicComputer.VerticalMode?): Boolean {
                return mode == null
            }
        };

        abstract fun matches(mode: AutopilotLogicComputer.VerticalMode?): Boolean
    }

    companion object {
        private val buttons: EnumMap<ButtonType, Button> = EnumMap(ButtonType::class.java)
        private val textFields: EnumMap<ButtonType, MutableList<EditBox>> = EnumMap(ButtonType::class.java)
        const val TOTAL_MODES: Float = 3.0f
    }
}
