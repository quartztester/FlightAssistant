package ru.octol1ttle.flightassistant.screen.flightplan

import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ParentElement
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.textRenderer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.AbstractParentWidget

class DepartureWaypointWidget(private val computers: ComputerView, val x: Int, val y: Int, val width: Int, val height: Int) : AbstractParentWidget(), FlightPlanState {
    private val displayText: TextWidget = TextWidget(x + 5, y + 8, width, 9, Text.translatable("menu.flightassistant.flight_plan.departure"), textRenderer).alignLeft()
    private val fieldWidth: Int = width / 3 - 4
    private val xField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width - fieldWidth * 2 - 8, y + 5, fieldWidth, 15, textFields[0], Text.empty()
    )
    private val zField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width - fieldWidth - 4, y + 5, fieldWidth, 15, textFields[1], Text.empty()
    )
    private var takeoffThrustText: TextWidget
    private val takeoffThrustField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + 15, y + 33, width / 2, 15, textFields[2], Text.empty()
    )
    private var thrustReductionAltitudeText: TextWidget
    private val thrustReductionAltitudeField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + 15, y + 66, width / 2, 15, textFields[3], Text.empty()
    )

    init {
        textFields[0] = xField
        textFields[1] = zField
        textFields[2] = takeoffThrustField
        textFields[3] = thrustReductionAltitudeField

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
        takeoffThrustText = TextWidget(takeoffThrustField.x, takeoffThrustField.y - 12, takeoffThrustField.width, 9, Text.translatable("menu.flightassistant.flight_plan.departure.takeoff_thrust"), textRenderer).alignLeft()
        thrustReductionAltitudeText = TextWidget(thrustReductionAltitudeField.x, thrustReductionAltitudeField.y - 12, thrustReductionAltitudeField.width, 9, Text.translatable("menu.flightassistant.flight_plan.departure.thrust_reduction_altitude"), textRenderer).alignLeft()
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
        if (isFocused) {
            context!!.drawBorder(x, y, width, height, 0xFFFFFFFF.toInt())
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
            list.add(takeoffThrustText)
            list.add(takeoffThrustField)
            list.add(thrustReductionAltitudeText)
            list.add(thrustReductionAltitudeField)
        }
        return list
    }

    override fun load() {
        for (field: TextFieldWidget? in textFields) {
            field?.text = ""
        }

        val waypoint: FlightPlanComputer.DepartureWaypoint = computers.plan.departureWaypoint ?: return
        xField.text = waypoint.x.toString()
        zField.text = waypoint.z.toString()
        takeoffThrustField.text = waypoint.takeoffThrust?.toString() ?: ""
        thrustReductionAltitudeField.text = waypoint.thrustReductionAltitude?.toString() ?: ""
    }

    override fun needsSaving(): Boolean {
        //TODO: toStringOrEmpty
        val waypoint: FlightPlanComputer.DepartureWaypoint = computers.plan.departureWaypoint ?: return textFields.any { it!!.text.isNotEmpty() }
        return !xField.text.equals(waypoint.x.toString()) || !zField.text.equals(waypoint.z.toString()) || !takeoffThrustField.text.equals(waypoint.takeoffThrust?.toString() ?: "") || !thrustReductionAltitudeField.text.equals(waypoint.takeoffThrust?.toString() ?: "")
    }

    override fun canSave(): Boolean {
        return xField.text.toDoubleOrNull() != null && zField.text.toDoubleOrNull() != null && (takeoffThrustField.text.toFloatOrNull() != null || thrustReductionAltitudeField.text.toDoubleOrNull() == null)
    }

    override fun save() {
        val x: Double = xField.text.toDouble()
        val z: Double = zField.text.toDouble()
        val takeoffThrust: Float? = takeoffThrustField.text.toFloatOrNull()
        val thrustReductionAltitude: Double? = thrustReductionAltitudeField.text.toDoubleOrNull()

        computers.plan.departureWaypoint = FlightPlanComputer.DepartureWaypoint(x, z, takeoffThrust, thrustReductionAltitude)

        load()
    }

    companion object {
        var textFields: MutableList<TextFieldWidget?> = mutableListOf(null, null, null, null)
    }
}
