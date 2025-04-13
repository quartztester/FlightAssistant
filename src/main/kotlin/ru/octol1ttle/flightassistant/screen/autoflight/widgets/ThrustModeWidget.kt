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

class ThrustModeWidget(val computers: ComputerView, val x: Int, val y: Int, val width: Int) : AbstractParentWidget(), DelayedApplyChanges {
    private val title: TextWidget = TextWidget(
        x, y, width, 20, Text.translatable("menu.flightassistant.autoflight.thrust"), mc.textRenderer
    )
    private var newType: ButtonType

    init {
        newType = ThrustModeWidget.ButtonType.entries.single { it.matches(computers.autopilot.thrustMode) }

        initSelectedSpeed()
        initVerticalTarget()

        buttons[ButtonType.FlightPlan] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.waypoint_thrust")
        ) { newType = ButtonType.FlightPlan }
            .dimensions(x + (width * (2 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()
    }

    private fun initSelectedSpeed() {
        val type = ButtonType.SelectedSpeed

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.selected_speed")
        ) { newType = type }
            .dimensions(x + 1, y + 20, width / 3 - 1, 15).build()
        val targetSpeedWidget = TextFieldWidget(
            mc.textRenderer, x + width / 4, y + 40, width / 2, 15, textFields[type]?.singleOrNull(), Text.empty()
        )
        targetSpeedWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.selected_speed"))
        targetSpeedWidget.setTextPredicate {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i > 0
        }
        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(targetSpeedWidget)
    }

    private fun initVerticalTarget() {
        val type = ButtonType.SelectedVerticalTarget

        buttons[type] = ButtonWidget.builder(
            Text.translatable("menu.flightassistant.autoflight.thrust.vertical_target")
        ) { newType = type }
            .dimensions(x + (width * (1 / TOTAL_MODES)).toInt() + 1, y + 20, width / 3 - 1, 15).build()

        val climbThrustWidget = TextFieldWidget(
            mc.textRenderer, x + 2, y + 40, width / 2 - 4, 15, textFields[type]?.getOrNull(0), Text.empty()
        )
        climbThrustWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.vertical_target.climb_thrust"))
        configureThrustWidget(climbThrustWidget)

        val descendThrustWidget = TextFieldWidget(
            mc.textRenderer, x + width / 2 + 3, y + 40, width / 2 - 4, 15, textFields[type]?.getOrNull(1), Text.empty()
        )
        descendThrustWidget.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.vertical_target.descend_thrust"))
        configureThrustWidget(descendThrustWidget)

        textFields.computeIfAbsent(type) { ArrayList() }.clearAndAdd(climbThrustWidget, descendThrustWidget)
    }

    private fun configureThrustWidget(thrustWidget: TextFieldWidget) {
        thrustWidget.setTextPredicate {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i in 0..100
        }
        thrustWidget.setChangedListener {
            if (it.toIntOrNull() == 100) {
                thrustWidget.text = "99"
            }
        }
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
        computers.autopilot.thrustMode = when (val type: ButtonType = newType) {
            ButtonType.SelectedSpeed -> {
                val speed: Float? = textFields[type]!!.single().text.toFloatOrNull()
                if (speed != null) AutopilotLogicComputer.SpeedThrustMode(speed) else computers.autopilot.thrustMode
            }
            ButtonType.SelectedVerticalTarget -> {
                val climbThrust: Int? = textFields[type]!![0].text.toIntOrNull()
                val descendThrust: Int? = textFields[type]!![1].text.toIntOrNull()
                if (climbThrust != null && descendThrust != null) AutopilotLogicComputer.VerticalTargetThrustMode(climbThrust / 100.0f, descendThrust / 100.0f) else computers.autopilot.thrustMode
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
        SelectedSpeed {
            override fun matches(mode: AutopilotLogicComputer.ThrustMode?): Boolean {
                return mode is AutopilotLogicComputer.SpeedThrustMode
            }
        },
        SelectedVerticalTarget {
            override fun matches(mode: AutopilotLogicComputer.ThrustMode?): Boolean {
                return mode is AutopilotLogicComputer.VerticalTargetThrustMode
            }
        },
        FlightPlan {
            override fun matches(mode: AutopilotLogicComputer.ThrustMode?): Boolean {
                return mode == null
            }
        };

        abstract fun matches(mode: AutopilotLogicComputer.ThrustMode?): Boolean
    }

    companion object {
        private val buttons: EnumMap<ButtonType, ButtonWidget> = EnumMap(ButtonType::class.java)
        private val textFields: EnumMap<ButtonType, MutableList<TextFieldWidget>> = EnumMap(ButtonType::class.java)
        const val TOTAL_MODES: Float = 3.0f
    }
}
