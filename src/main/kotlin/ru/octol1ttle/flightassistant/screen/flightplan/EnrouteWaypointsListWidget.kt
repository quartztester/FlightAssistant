package ru.octol1ttle.flightassistant.screen.flightplan

import com.google.common.collect.ImmutableList
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.TextFieldWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedText
import ru.octol1ttle.flightassistant.api.util.extensions.textRenderer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.FlightPlanComputer
import ru.octol1ttle.flightassistant.screen.autoflight.widgets.ThrustModeWidget.ButtonType

// TODO: REWRITE THIS ABSOLUTE FUCKY SHITTY HORRIBLE GARBAGE YOU CALL "CODE"
class EnrouteWaypointsListWidget(private val computers: ComputerView, width: Int, height: Int, top: Int, @Suppress("UNUSED_PARAMETER", "KotlinRedundantDiagnosticSuppress") bottom: Int, val left: Int) : ElementListWidget<EnrouteWaypointsListWidget.AbstractEntry>(
    mc, width, height, top,
    /*? if <1.21 {*/ bottom, //?}
    ITEM_HEIGHT
), FlightPlanState {

    init {
//? if <1.21 {
        setRenderBackground(false)
        setRenderHorizontalShadows(false)
//?}

        rebuildEntries()
    }

//? if >=1.21 {
    /*override fun getScrollbarX(): Int {
        return this.x + this.width - 4
    }
*///?} else {
    override fun getScrollbarPositionX(): Int {
    return this.left + this.width + 4
    }
//?}

    override fun getRowWidth(): Int {
        return this.width
    }

    override fun isSelectedEntry(index: Int): Boolean {
        return this.selectedOrNull == children()[index]
    }

    private fun rebuildEntries() {
        this.clearEntries()

        val x: Int = left + OFFSET
        var y: Int = OFFSET
        var order = 1
        this.addEntry(
            AddWaypointButtonEntry(
                order, left, y, width
            )
        )
        for (state: EnrouteWaypointState in states) {
            this.addEntry(
                WaypointEntry(
                    state, order, x, y, width
                )
            )
            this.addEntry(
                AddWaypointButtonEntry(
                    order, x, y, width
                )
            )
            order++
            y += ITEM_HEIGHT
        }
    }

    abstract class AbstractEntry(val x: Int, val y: Int, val width: Int) : Entry<AbstractEntry>()

    inner class AddWaypointButtonEntry(private val order: Int, x: Int, y: Int, width: Int) : AbstractEntry(x, y, width) {
        override fun render(context: DrawContext, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            val renderY: Int = y + ITEM_HEIGHT / 2
            context.drawHorizontalLine(x + OFFSET, x + entryWidth - OFFSET * 2, renderY, if (hovered) 0x7FFFFFFF else 0x40FFFFFF)
            if (hovered) {
                context.drawMiddleAlignedText(Text.translatable("menu.flightassistant.flight_plan.add_waypoint"), x + entryWidth / 2, renderY - 4, 0xFFFFFFFF.toInt())
            }
        }

        override fun children(): MutableList<out Element> {
            return mutableListOf()
        }

        override fun selectableChildren(): MutableList<out Selectable> {
            return mutableListOf()
        }

        override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
            val new = EnrouteWaypointState(null, null, null, null, ButtonType.SelectedSpeed, null, null)
            if (order >= states.size) {
                states.add(new)
            } else {
                states.add(order, new)
            }
            this@EnrouteWaypointsListWidget.rebuildEntries()
            return true
        }
    }

    class WaypointEntry(private val state: EnrouteWaypointState, order: Int, x: Int, y: Int, width: Int) : AbstractEntry(x, y, width) {
        private val displayText: TextWidget = TextWidget(x + 5, y, width / 3, 9, Text.translatable("menu.flightassistant.flight_plan.enroute", order), textRenderer).alignLeft()
        private val fieldWidth: Int = width / 3 - 4
        private val xField: TextFieldWidget = TextFieldWidget(
            mc.textRenderer, x + width - fieldWidth * 2 - 8, y, fieldWidth, 15, null, Text.empty()
        )
        private val zField: TextFieldWidget = TextFieldWidget(
            mc.textRenderer, x + width - fieldWidth - 4, y, fieldWidth, 15, null, Text.empty()
        )

        init {
            xField.text = state.x?.toString() ?: ""
            xField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.target_x"))
            xField.setTextPredicate {
                val i: Double? = it.toDoubleOrNull()
                it.isEmpty() || it == "-" || i != null
            }
            xField.setChangedListener { state.x = it.toDoubleOrNull() }
            zField.text = state.z?.toString() ?: ""
            zField.setPlaceholder(Text.translatable("menu.flightassistant.autoflight.target_z"))
            zField.setTextPredicate {
                val i: Double? = it.toDoubleOrNull()
                it.isEmpty() || it == "-" || i != null
            }
            zField.setChangedListener { state.z = it.toDoubleOrNull() }
        }

        override fun render(context: DrawContext, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            val renderY: Int = y + OFFSET

            displayText.y = renderY + 1
            displayText.render(context, mouseX, mouseY, tickDelta)

            xField.y = renderY - 2
            xField.render(context, mouseX, mouseY, tickDelta)
            zField.y = renderY - 2
            zField.render(context, mouseX, mouseY, tickDelta)
        }

        override fun children(): MutableList<out Element> {
            return ImmutableList.of(displayText, xField, zField)
        }

        override fun selectableChildren(): MutableList<out Selectable> {
            return ImmutableList.of(displayText, xField, zField)
        }
    }

    override fun load() {
        states.clear()
        val waypoints: List<FlightPlanComputer.EnrouteWaypoint> = computers.plan.enrouteWaypoints
        for (waypoint: FlightPlanComputer.EnrouteWaypoint in waypoints) {
            states.add(
                EnrouteWaypointState(
                    waypoint, waypoint.x, waypoint.z, waypoint.altitude, ButtonType.entries.single { it.matches(waypoint.thrustMode) }, when (val mode = waypoint.thrustMode) {
                        is AutopilotLogicComputer.SpeedThrustMode -> mode.speed
                        is AutopilotLogicComputer.VerticalTargetThrustMode -> mode.climbThrust
                        else -> throw UnsupportedOperationException()
                    }, when (val mode = waypoint.thrustMode) {
                        is AutopilotLogicComputer.SpeedThrustMode -> null
                        is AutopilotLogicComputer.VerticalTargetThrustMode -> mode.descendThrust
                        else -> throw UnsupportedOperationException()
                    }
                )
            )
        }

        rebuildEntries()
    }

    override fun needsSaving(): Boolean {
        return states.any { it.needsSaving() }
    }

    override fun canSave(): Boolean {
        return states.all { it.canSave() }
    }

    override fun save() {
        computers.plan.enrouteWaypoints.clear()

        for (state: EnrouteWaypointState in states) {
            computers.plan.enrouteWaypoints.add(
                FlightPlanComputer.EnrouteWaypoint(
                    state.x!!, state.z!!, state.altitude!!, when (state.thrustModeType) {
                        ButtonType.SelectedSpeed -> {
                            val speed: Float = state.thrustField1!!
                            AutopilotLogicComputer.SpeedThrustMode(speed)
                        }

                        ButtonType.SelectedVerticalTarget -> {
                            val climbThrust: Int = state.thrustField1!!.toInt()
                            val descendThrust: Int = state.thrustField2!!.toInt()
                            AutopilotLogicComputer.VerticalTargetThrustMode(climbThrust / 100.0f, descendThrust / 100.0f)
                        }

                        else -> throw UnsupportedOperationException()
                    }
                )
            )
        }
    }

    class EnrouteWaypointState(private val linkedWaypoint: FlightPlanComputer.EnrouteWaypoint?, var x: Double?, var z: Double?, var altitude: Double?, var thrustModeType: ButtonType, var thrustField1: Float?, var thrustField2: Float?) : FlightPlanState {
        override fun load(): Unit = throw UnsupportedOperationException()

        override fun needsSaving(): Boolean {
            if (linkedWaypoint == null) {
                return true
            }

            val thrustMode: AutopilotLogicComputer.ThrustMode = linkedWaypoint.thrustMode
            if (this.x != linkedWaypoint.x || this.z != linkedWaypoint.z || !thrustModeType.matches(thrustMode) || this.altitude != linkedWaypoint.altitude) {
                return true
            }
            if (when (thrustMode) {
                    is AutopilotLogicComputer.SpeedThrustMode -> thrustMode.speed != thrustField1
                    is AutopilotLogicComputer.VerticalTargetThrustMode -> thrustMode.climbThrust != thrustField1 || thrustMode.descendThrust != thrustField2
                    else -> throw UnsupportedOperationException()
                }
            ) {
                return true
            }

            return false
        }

        override fun canSave(): Boolean {
            return this.x != null && this.z != null && this.altitude != null && thrustField1 != null && (thrustModeType == ButtonType.SelectedSpeed || thrustField2 != null)
        }

        override fun save(): Unit = throw UnsupportedOperationException()
    }

    companion object {
        const val OFFSET: Int = 5
        const val ITEM_HEIGHT: Int = 25
        val states: MutableList<EnrouteWaypointState> = ArrayList()
    }
}
