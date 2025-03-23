package ru.octol1ttle.flightassistant.screen.flightplan

import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.util.extensions.textRenderer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.AbstractParentWidget

class DepartureWaypointWidget(val waypoint: FlightPlanComputer.DepartureWaypoint, val x: Int, val y: Int, val width: Int, val height: Int) : AbstractParentWidget() {
    private val displayName: TextWidget = TextWidget(x, y, width, 9, Text.translatable("menu.flightassistant.flight_plan.departure_waypoint"), textRenderer).alignLeft()
    private val xField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x, y + 11, width / 2 - 4, 15, xFieldOld, Text.empty()
    )
    private val zField: TextFieldWidget = TextFieldWidget(
        mc.textRenderer, x + width / 2, y + 11, width / 2 - 4, 15, zFieldOld, Text.empty()
    )

    init {
        xFieldOld = xField
        zFieldOld = zField

        xField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.target_x"))
        xField.setTextPredicate {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }
        zField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.target_z"))
        zField.setTextPredicate {
            val i: Double? = it.toDoubleOrNull()
            it.isEmpty() || it == "-" || i != null
        }
    }

    override fun children(): MutableList<out Element> {
        return ImmutableList.of(displayName, xField, zField)
    }

    companion object {
        var xFieldOld: TextFieldWidget? = null
        var zFieldOld: TextFieldWidget? = null
    }
}
