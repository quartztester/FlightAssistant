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
import ru.octol1ttle.flightassistant.screen.autoflight.widgets.ThrustModeWidget

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
        mc.textRenderer, x + width + 15 + fieldWidth / 2, extraY + 80, fieldWidth, 15, textFields[3], Text.empty()
    )
    private val constantThrustField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + 15 + fieldWidth / 2, extraY + 80, fieldWidth, 15, textFields[4], Text.empty()
    )

    private var minimumsText: TextWidget
    private val absoluteMinimumsButton: ButtonWidget = ButtonWidget.builder(
        Text.translatable("menu.flightassistant.flight_plan.arrival.minimums.absolute")
    ) { isAbsoluteMinimums = true }
        .dimensions(x + width + 15, extraY + 115, fieldWidth, 15).build()
    private val relativeMinimumsButton: ButtonWidget = ButtonWidget.builder(
        Text.translatable("menu.flightassistant.flight_plan.arrival.minimums.relative")
    ) { isAbsoluteMinimums = false }
        .dimensions(x + width + fieldWidth + 20, extraY + 115, fieldWidth, 15).build()
    private val absoluteMinimumsField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + 15 + fieldWidth / 2, extraY + 135, fieldWidth, 15, textFields[5], Text.empty()
    )
    private val relativeMinimumsField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + 15 + fieldWidth / 2, extraY + 135, fieldWidth, 15, textFields[6], Text.empty()
    )

    private var goAroundAltitudeText: TextWidget
    private val goAroundAltitudeField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width + fieldWidth + 20, extraY + 33, fieldWidth, 15, textFields[7], Text.empty()
    )

    init {
        textFields[0] = xField
        textFields[1] = zField
        textFields[2] = landingAltitudeField
        textFields[3] = speedField
        textFields[4] = constantThrustField
        textFields[5] = absoluteMinimumsField
        textFields[6] = relativeMinimumsField
        textFields[7] = goAroundAltitudeField

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
            val i: Float? = it.toFloatOrNull()
            it.isEmpty() || i != null && i > 0.0f
        }

        constantThrustField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.thrust.target_thrust"))
        constantThrustField.setTextPredicate {
            val i: Float? = it.toFloatOrNull()
            it.isEmpty() || i != null && i in 0.0f..100.0f
        }
        constantThrustField.setChangedListener {
            if (it.toFloatOrNull() == 100.0f) {
                constantThrustField.text = "99"
            }
        }

        minimumsText = TextWidget(absoluteMinimumsButton.x, absoluteMinimumsButton.y - 12, absoluteMinimumsButton.width, 9, Text.translatable("menu.flightassistant.flight_plan.arrival.minimums"), textRenderer).alignLeft()
        absoluteMinimumsField.setTextPredicate {
            it.isEmpty() || it == "-" || it.toDoubleOrNull() != null
        }
        relativeMinimumsField.setTextPredicate {
            val i: Float? = it.toFloatOrNull()
            it.isEmpty() || i != null && i > 0.0f
        }

        goAroundAltitudeText = TextWidget(goAroundAltitudeField.x, goAroundAltitudeField.y - 12, goAroundAltitudeField.width, 9, Text.translatable("menu.flightassistant.flight_plan.arrival.go_around_altitude"), textRenderer).alignLeft()
        goAroundAltitudeField.setTextPredicate {
            it.isEmpty() || it == "-" || it.toDoubleOrNull() != null
        }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        speedButton.active = !isSpeedMode
        constantThrustButton.active = isSpeedMode
        absoluteMinimumsButton.active = !isAbsoluteMinimums
        relativeMinimumsButton.active = isAbsoluteMinimums

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
            list.add(landingAltitudeText)
            list.add(landingAltitudeField)
            list.add(speedButton)
            list.add(constantThrustButton)
            if (isSpeedMode) {
                list.add(speedField)
            } else {
                list.add(constantThrustField)
            }
            list.add(minimumsText)
            list.add(absoluteMinimumsButton)
            list.add(relativeMinimumsButton)
            if (isAbsoluteMinimums) {
                list.add(absoluteMinimumsField)
            } else {
                list.add(relativeMinimumsField)
            }
            list.add(goAroundAltitudeText)
            list.add(goAroundAltitudeField)
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
        isSpeedMode = ThrustModeWidget.ButtonType.SelectedSpeed.matches(waypoint.thrustMode)
        if (waypoint.thrustMode is AutopilotLogicComputer.SpeedThrustMode) {
            speedField.text = waypoint.thrustMode.speed.toString()
        }
        if (waypoint.thrustMode is AutopilotLogicComputer.VerticalTargetThrustMode) {
            constantThrustField.text = (waypoint.thrustMode.descendThrust * 100.0f).toString()
        }
        isAbsoluteMinimums = waypoint.minimums?.type != FlightPlanComputer.Minimums.Type.ABOVE_GROUND
        if (isAbsoluteMinimums) {
            absoluteMinimumsField.text = waypoint.minimums?.value?.toString() ?: ""
        } else {
            relativeMinimumsField.text = waypoint.minimums?.value?.toString() ?: ""
        }
        goAroundAltitudeField.text = waypoint.goAroundAltitude?.toString() ?: ""
    }

    override fun needsSaving(): Boolean {
        //TODO: toStringOrEmpty
        val waypoint: FlightPlanComputer.ArrivalWaypoint = computers.plan.arrivalWaypoint ?: return textFields.any { it!!.text.isNotEmpty() }

        val anyCoordinateEmpty: Boolean = !xField.text.equals(waypoint.x.toString()) || !zField.text.equals(waypoint.z.toString())

        val landingAltitudeChanged: Boolean = !landingAltitudeField.text.equals(waypoint.landingAltitude?.toString() ?: "")

        val hasThrustMode: Boolean = waypoint.thrustMode != null
        val thrustModeSet: Boolean = !hasThrustMode && (if (isSpeedMode) speedField.text.isNotEmpty() else constantThrustField.text.isNotEmpty())
        val thrustModeTypeChanged: Boolean = hasThrustMode && (isSpeedMode != waypoint.thrustMode is AutopilotLogicComputer.SpeedThrustMode)
        val speedChanged: Boolean = hasThrustMode && (waypoint.thrustMode is AutopilotLogicComputer.SpeedThrustMode && !speedField.text.equals(waypoint.thrustMode.speed.toString()))
        val thrustChanged: Boolean = hasThrustMode && (waypoint.thrustMode is AutopilotLogicComputer.VerticalTargetThrustMode && !constantThrustField.text.equals((waypoint.thrustMode.descendThrust * 100.0f).toString()))

        val hasMinimums: Boolean = waypoint.minimums != null
        val minimumsSet: Boolean = !hasMinimums && (if (isAbsoluteMinimums) absoluteMinimumsField.text.isNotEmpty() else relativeMinimumsField.text.isNotEmpty())
        val minimumsTypeChanged: Boolean = hasMinimums && (isAbsoluteMinimums != (waypoint.minimums?.type == FlightPlanComputer.Minimums.Type.ABSOLUTE))
        val minimumsChanged: Boolean = hasMinimums && !(if (isAbsoluteMinimums) absoluteMinimumsField.text else relativeMinimumsField.text).equals(waypoint.minimums?.value?.toString() ?: "")
        val goAroundAltitudeChanged: Boolean = !goAroundAltitudeField.text.equals(waypoint.goAroundAltitude?.toString() ?: "")
        return anyCoordinateEmpty || landingAltitudeChanged || thrustModeSet || thrustModeTypeChanged || speedChanged || thrustChanged
                || minimumsSet
                || minimumsTypeChanged
                || minimumsChanged
                || goAroundAltitudeChanged
    }

    override fun canSave(): Boolean {
        return xField.text.toDoubleOrNull() != null && zField.text.toDoubleOrNull() != null && (landingAltitudeField.text.isEmpty() || landingAltitudeField.text.toDoubleOrNull() != null) && (if (isSpeedMode) speedField.text.isEmpty() || speedField.text.toIntOrNull() != null else constantThrustField.text.isEmpty() || constantThrustField.text.toFloatOrNull() != null)
                && (if (isAbsoluteMinimums) absoluteMinimumsField.text.isEmpty() || absoluteMinimumsField.text.toDoubleOrNull() != null else relativeMinimumsField.text.isEmpty() || relativeMinimumsField.text.toDoubleOrNull() != null)
                && goAroundAltitudeField.text.isEmpty() || goAroundAltitudeField.text.toDoubleOrNull() != null
    }

    override fun save() {
        val x: Double = xField.text.toDouble()
        val z: Double = zField.text.toDouble()
        val landingAltitude: Double? = landingAltitudeField.text.toDoubleOrNull()
        val thrustMode: AutopilotLogicComputer.ThrustMode? = if (isSpeedMode)
            if (speedField.text.toFloatOrNull() != null) AutopilotLogicComputer.SpeedThrustMode(speedField.text.toFloat())
            else null
        else
            if (constantThrustField.text.toFloatOrNull() != null) AutopilotLogicComputer.VerticalTargetThrustMode(0.0f, constantThrustField.text.toFloat() / 100.0f)
            else null
        val minimums: FlightPlanComputer.Minimums? =
            if (isAbsoluteMinimums) if (absoluteMinimumsField.text.isEmpty()) null else FlightPlanComputer.Minimums(FlightPlanComputer.Minimums.Type.ABSOLUTE, absoluteMinimumsField.text.toDouble())
            else if (relativeMinimumsField.text.isEmpty()) null else FlightPlanComputer.Minimums(FlightPlanComputer.Minimums.Type.ABOVE_GROUND, relativeMinimumsField.text.toDouble())
        val goAroundAltitude: Double? = goAroundAltitudeField.text.toDoubleOrNull()

        computers.plan.arrivalWaypoint = FlightPlanComputer.ArrivalWaypoint(x, z, landingAltitude, thrustMode, minimums, goAroundAltitude, null)

        load()
    }

    companion object {
        var textFields: MutableList<TextFieldWidget?> = mutableListOf(null, null, null, null, null, null, null, null)
        var isSpeedMode: Boolean = true
        var isAbsoluteMinimums: Boolean = true
    }
}
