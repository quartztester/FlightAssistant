package ru.octol1ttle.flightassistant.screen.flightplan

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ParentElement
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.textRenderer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.AbstractParentWidget

// TODO: REWRITE THIS ABSOLUTE FUCKY SHITTY HORRIBLE GARBAGE YOU CALL "CODE"
class ArrivalWaypointWidget(private val computers: ComputerView, val x: Int, val y: Int, val width: Int, val height: Int, extraY: Int) : AbstractParentWidget(), FlightPlanState {
    private val displayText: TextWidget = TextWidget(x + 5, y + 8, width, 9, Text.translatable("menu.flightassistant.flight_plan.arrival"), textRenderer).alignLeft()
    private val fieldWidth: Int = width / 3 - 4
    private val xField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width - fieldWidth * 2 - 8, y + 5, fieldWidth, 15, textFields[0], Text.empty()
    )
    private val zField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width - fieldWidth - 4, y + 5, fieldWidth, 15, textFields[1], Text.empty()
    )
    private var landingAltitudeText: TextWidget
    private val landingAltitudeField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + 15, extraY + 33, fieldWidth, 15, textFields[2], Text.empty()
    )

    private val speedButton: ButtonWidget = ButtonWidget.builder(
        Text.translatable("menu.flightassistant.autoflight.thrust.selected_speed")
    ) { isSpeedMode = true }
        .dimensions(x + width + 15, extraY + 60, fieldWidth, 15).build()
    private val constantThrustButton: ButtonWidget = ButtonWidget.builder(
        Text.translatable("menu.flightassistant.autoflight.thrust.constant_thrust")
    ) { isSpeedMode = false }
        .dimensions(x + width + fieldWidth + 20, extraY + 60, fieldWidth, 15).build()
    private val speedField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + 15 + fieldWidth / 2, extraY + 80, fieldWidth, 15, null, Text.empty()
    )
    private val constantThrustField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + 15 + fieldWidth / 2, extraY + 80, fieldWidth, 15, null, Text.empty()
    )

    init {
        textFields[0] = xField
        textFields[1] = zField
        textFields[2] = landingAltitudeField
        textFields[3] = speedField
        textFields[4] = constantThrustField

        xField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.lateral.target_x"))
        xField.setTextPredicate {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }
        zField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.lateral.target_z"))
        zField.setTextPredicate {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }
        landingAltitudeText = TextWidget(landingAltitudeField.x, landingAltitudeField.y - 12, landingAltitudeField.width, 9, Text.translatable("menu.flightassistant.flight_plan.arrival.landing_altitude"), textRenderer).alignLeft()
        landingAltitudeField.setTextPredicate {
            it.isEmpty() || it == "-" || it.toDoubleOrNull() != null
        }

        speedField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.target_speed"))
        speedField.setTextPredicate {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i > 0
        }

        constantThrustField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.target_thrust"))
        constantThrustField.setTextPredicate {
            val i: Int? = it.toIntOrNull()
            it.isEmpty() || i != null && i in 0..100
        }
        constantThrustField.setChangedListener {
            if (it.toIntOrNull() == 100) {
                constantThrustField.text = "99"
            }
        }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        if (isFocused) {
            context!!.drawBorder(x, y, width, height, 0xFFFFFFFF.toInt())
            speedButton.active = !isSpeedMode
            constantThrustButton.active = isSpeedMode
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        this.forceFocused = super.mouseClicked(mouseX, mouseY, button) || (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height)
        if (!this.forceFocused) {
            val focused: Element? = this.focused
            if (focused is ParentElement) {
                focused.focused = null
            }
            this.focused = null
        }
        return this.forceFocused
    }

    override fun children(): MutableList<out Element> {
        val list = ArrayList<Element>()
        list.add(displayText)
        list.add(xField)
        list.add(zField)
        if (isFocused) {
            list.add(landingAltitudeText)
            list.add(landingAltitudeField)
            list.add(speedButton)
            list.add(constantThrustButton)
            if (isSpeedMode) {
                list.add(speedField)
            } else {
                list.add(constantThrustField)
            }
        }
        return list
    }

    override fun load() {
        for (field: TextFieldWidget? in textFields) {
            field?.text = ""
        }

        val waypoint: FlightPlanComputer.ArrivalWaypoint = computers.plan.arrivalWaypoint ?: return
        xField.text = waypoint.x.toString()
        zField.text = waypoint.z.toString()
        landingAltitudeField.text = waypoint.landingAltitude?.toString() ?: ""
    }

    override fun needsSaving(): Boolean {
        //TODO: toStringOrEmpty
        val waypoint: FlightPlanComputer.ArrivalWaypoint = computers.plan.arrivalWaypoint ?: return textFields.any { it!!.text.isNotEmpty() }
        return !xField.text.equals(waypoint.x.toString()) || !zField.text.equals(waypoint.z.toString()) || !landingAltitudeField.text.equals(waypoint.landingAltitude?.toString() ?: "")
    }

    override fun canSave(): Boolean {
        return xField.text.toDoubleOrNull() != null && zField.text.toDoubleOrNull() != null && (landingAltitudeField.text.isEmpty() || landingAltitudeField.text.toDoubleOrNull() != null) && (if (isSpeedMode) speedField.text.isEmpty() || speedField.text.toIntOrNull() != null else constantThrustField.text.isEmpty() || constantThrustField.text.toFloatOrNull() != null)
    }

    override fun save() {
        val x: Double = xField.text.toDouble()
        val z: Double = zField.text.toDouble()
        val landingAltitude: Double? = landingAltitudeField.text.toDoubleOrNull()
        val thrustMode: AutopilotLogicComputer.ThrustMode? = if (isSpeedMode)
            if (speedField.text.toFloatOrNull() != null) AutopilotLogicComputer.SpeedThrustMode(speedField.text.toFloat())
            else null
        else
            if (constantThrustField.text.toFloatOrNull() != null) AutopilotLogicComputer.VerticalTargetThrustMode(0.0f, constantThrustField.text.toFloat())
            else null

        computers.plan.arrivalWaypoint = FlightPlanComputer.ArrivalWaypoint(x, z, landingAltitude, thrustMode, null, null, null)

        load()
    }

    companion object {
        var textFields: MutableList<TextFieldWidget?> = mutableListOf(null, null, null, null, null)
        var isSpeedMode: Boolean = true
    }
}
