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

// TODO: fix dimensions
class ThrustModeWidget(val computers: ComputerView, val x: Int, val y: Int, val width: Int) : AbstractParentWidget(), AutoCloseable {
    private val title: TextWidget = TextWidget(
        x, y, width, 20, Text.translatable("menu.flightassistant.autoflight.thrust"), mc.textRenderer
    )
    private var newType: AutopilotLogicComputer.ThrustMode.Type = computers.autopilot.thrustMode.type

    init {
        initSelectedSpeed()
        initVerticalTarget()

        buttons[AutopilotLogicComputer.ThrustMode.Type.WaypointThrust] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.waypoint_thrust")
        ) { newType = AutopilotLogicComputer.ThrustMode.Type.WaypointThrust }
            .dimensions(x, y + 20, width / 3 - 5, 15).build()
    }

    private fun initSelectedSpeed() {
        val type = AutopilotLogicComputer.ThrustMode.Type.SelectedSpeed

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.selected_speed")
        ) { newType = type }
            .dimensions(x, y + 20, (width - 15) / 3, 15).build()
        val targetSpeedWidget = TextFieldWidget(
            mc.textRenderer, x + width / 4, y + 40, width / 2, 15, textFields[type]?.singleOrNull(), Text.empty()
        )
        targetSpeedWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.selected_speed.target"))
        targetSpeedWidget.setTextPredicate {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i > 0
        }
        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(targetSpeedWidget)
    }

    private fun initVerticalTarget() {
        val type = AutopilotLogicComputer.ThrustMode.Type.VerticalTarget

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.vertical_target")
        ) { newType = type }
            .dimensions(x + (width - 15) / 3, y + 20, (width - 15) / 3, 15).build()

        val climbThrustWidget = TextFieldWidget(
            mc.textRenderer, x, y + 40, (width - 10) / 2, 15, textFields[type]?.firstOrNull(), Text.empty()
        )
        climbThrustWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.vertical_target.climb_thrust"))
        climbThrustWidget.setTextPredicate {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i in 0..100
        }

        val descendThrustWidget = TextFieldWidget(
            mc.textRenderer, x + width / 2, y + 40, (width - 10) / 2, 15, textFields[type]?.firstOrNull(), Text.empty()
        )
        descendThrustWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.vertical_target.descend_thrust"))
        descendThrustWidget.setTextPredicate {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i in 0..100
        }

        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(climbThrustWidget, descendThrustWidget)
    }

    override fun children(): MutableList<out Element> {
        val list = ArrayList<Element>()
        list.add(title)
        list.addAll(buttons.values)
        textFields[computers.autopilot.thrustMode.type]?.let {
            list.addAll(it)
        }
        return list
    }

    override fun close() {
        computers.autopilot.thrustMode.type = newType
        when (val type: AutopilotLogicComputer.ThrustMode.Type = computers.autopilot.thrustMode.type) {
            AutopilotLogicComputer.ThrustMode.Type.SelectedSpeed -> computers.autopilot.thrustMode.speed = textFields[type]!!.single().text.toFloatOrNull() ?: 0.0f
            AutopilotLogicComputer.ThrustMode.Type.VerticalTarget -> {
                computers.autopilot.thrustMode.climbThrust = (textFields[type]!!.first().text.toIntOrNull() ?: 0) / 100.0f
                computers.autopilot.thrustMode.descendThrust = (textFields[type]!![1].text.toIntOrNull() ?: 0) / 100.0f
            }
            else -> Unit
        }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        for (button in buttons) {
            button.value.active = computers.autopilot.thrustMode.type != button.key
        }

        super.render(context, mouseX, mouseY, delta)
    }

    companion object {
        private val buttons: EnumMap<AutopilotLogicComputer.ThrustMode.Type, ButtonWidget> = EnumMap(AutopilotLogicComputer.ThrustMode.Type::class.java)
        private val textFields: EnumMap<AutopilotLogicComputer.ThrustMode.Type, MutableList<TextFieldWidget>> = EnumMap(AutopilotLogicComputer.ThrustMode.Type::class.java)
    }
}
