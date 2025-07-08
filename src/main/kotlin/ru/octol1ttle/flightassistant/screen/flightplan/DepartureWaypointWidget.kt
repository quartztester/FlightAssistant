package ru.octol1ttle.flightassistant.screen.flightplan

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.ParentElement
import net.minecraft.client.gui.widget.EditBox
import net.minecraft.client.gui.widget.StringWidget
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.font
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.AbstractParentWidget

// TODO: REWRITE THIS ABSOLUTE FUCKY SHITTY HORRIBLE GARBAGE YOU CALL "CODE"
class DepartureWaypointWidget(private val computers: ComputerView, val x: Int, val y: Int, val width: Int, val height: Int) : AbstractParentWidget(), FlightPlanState {
    private val displayText: StringWidget = StringWidget(x + 5, y + 8, width, 9, Component.translatable("menu.flightassistant.flight_plan.departure"), font).alignLeft()
    private val fieldWidth: Int = width / 3 - 4
    private val xField: EditBox = EditBox(
        mc.font, x + width - fieldWidth * 2 - 8, y + 5, fieldWidth, 15, textFields[0], Component.empty()
    )
    private val zField: EditBox = EditBox(
        mc.font, x + width - fieldWidth - 4, y + 5, fieldWidth, 15, textFields[1], Component.empty()
    )
    private var takeoffThrustText: StringWidget
    private val takeoffThrustField: EditBox = EditBox(
        mc.font, x + width + 15, y + 33, width / 2, 15, textFields[2], Component.empty()
    )
    private var thrustReductionAltitudeText: StringWidget
    private val thrustReductionAltitudeField: EditBox = EditBox(
        mc.font, x + width + 15, y + 66, width / 2, 15, textFields[3], Component.empty()
    )

    init {
        textFields[0] = xField
        textFields[1] = zField
        textFields[2] = takeoffThrustField
        textFields[3] = thrustReductionAltitudeField

        xField.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.lateral.target_x"))
        xField.setTextPredicate {
            it.isEmpty() || it == "-" || it.toDoubleOrNull() != null
        }
        zField.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.lateral.target_z"))
        zField.setTextPredicate {
            it.isEmpty() || it == "-" || it.toDoubleOrNull() != null
        }

        takeoffThrustText = StringWidget(takeoffThrustField.x, takeoffThrustField.y - 12, takeoffThrustField.width, 9, Component.translatable("menu.flightassistant.flight_plan.departure.takeoff_thrust"), font).alignLeft()
        takeoffThrustField.setTextPredicate {
            val i: Float? = it.toFloatOrNull()
            it.isEmpty() || i != null && i in 0.0f..100.0f
        }

        thrustReductionAltitudeText = StringWidget(thrustReductionAltitudeField.x, thrustReductionAltitudeField.y - 12, thrustReductionAltitudeField.width, 9, Component.translatable("menu.flightassistant.flight_plan.departure.thrust_reduction_altitude"), font).alignLeft()
        thrustReductionAltitudeField.setTextPredicate {
            it.isEmpty() || it == "-" || it.toFloatOrNull() != null
        }
    }

    override fun render(context: GuiGraphics?, mouseX: Int, mouseY: Int, delta: Float) {
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
        for (field: EditBox? in textFields) {
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
        return !xField.text.equals(waypoint.x.toString()) || !zField.text.equals(waypoint.z.toString()) || !takeoffThrustField.text.equals(waypoint.takeoffThrust?.toString() ?: "") || !thrustReductionAltitudeField.text.equals(waypoint.thrustReductionAltitude?.toString() ?: "")
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
        var textFields: MutableList<EditBox?> = mutableListOf(null, null, null, null)
    }
}
