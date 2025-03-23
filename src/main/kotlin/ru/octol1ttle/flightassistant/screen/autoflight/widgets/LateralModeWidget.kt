package ru.octol1ttle.flightassistant.screen.autoflight.widgets

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
import ru.octol1ttle.flightassistant.screen.AbstractParentWidget

class LateralModeWidget(val computers: ComputerView, val x: Int, val y: Int, val width: Int) : AbstractParentWidget(), DelayedApplyChanges {
    private val title: TextWidget = TextWidget(
        x, y, width, 20, Text.translatable("menu.flightassistant.autoflight.lateral"), mc.textRenderer
    )
    private var newType: ButtonType

    init {
        newType = ButtonType.entries.single { it.matches(computers.autopilot.lateralMode) }

        initSelectedHeading()
        initSelectedCoordinates()

        buttons[ButtonType.FlightPlan] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.lateral.waypoint_coordinates")
        ) { newType = ButtonType.FlightPlan }
            .dimensions(x + (width * (2 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()
    }

    private fun initSelectedHeading() {
        val type = ButtonType.SelectedHeading

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
        val type = ButtonType.SelectedCoordinates

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.lateral.selected_coordinates")
        ) { newType = type }
            .dimensions(x + (width * (1 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()

        val xCoordWidget = TextFieldWidget(
            mc.textRenderer, x + 2, y + 40, width / 2 - 4, 15, textFields[type]?.getOrNull(0), Text.empty()
        )
        xCoordWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.target_x"))
        xCoordWidget.setTextPredicate {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }

        val zCoordWidget = TextFieldWidget(
            mc.textRenderer, x + width / 2 + 3, y + 40, width / 2 - 4, 15, textFields[type]?.getOrNull(1), Text.empty()
        )
        zCoordWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.target_z"))
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
        computers.autopilot.lateralMode = when (val type: ButtonType = newType) {
            ButtonType.SelectedHeading -> {
                val heading: Float? = textFields[type]!!.single().text.toFloatOrNull()
                if (heading != null) AutopilotLogicComputer.HeadingLateralMode(heading) else computers.autopilot.lateralMode
            }
            ButtonType.SelectedCoordinates -> {
                val x: Double? = textFields[type]!![0].text.toDoubleOrNull()
                val z: Double? = textFields[type]!![1].text.toDoubleOrNull()
                if (x != null && z != null) AutopilotLogicComputer.CoordinatesLateralMode(x, z) else computers.autopilot.lateralMode
            }
            ButtonType.FlightPlan -> null
        }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
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
        private val buttons: EnumMap<ButtonType, ButtonWidget> = EnumMap(ButtonType::class.java)
        private val textFields: EnumMap<ButtonType, MutableList<TextFieldWidget>> = EnumMap(ButtonType::class.java)
        const val TOTAL_MODES: Float = 3.0f
    }
}
