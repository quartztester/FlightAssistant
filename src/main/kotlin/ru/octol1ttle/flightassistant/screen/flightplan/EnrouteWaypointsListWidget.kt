package ru.octol1ttle.flightassistant.screen.flightplan

import kotlinx.coroutines.NonCancellable.children
import net.minecraft.client.gui.Element
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.Selectable
import net.minecraft.client.gui.widget.Button
import net.minecraft.client.gui.widget.EditBox
import net.minecraft.client.gui.widget.ElementListWidget
import net.minecraft.client.gui.widget.StringWidget
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.extensions.drawMiddleAlignedString
import ru.octol1ttle.flightassistant.api.util.extensions.font
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

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            return focused?.mouseClicked(mouseX, mouseY, button) ?: false
        }
        return true
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
        override fun render(context: GuiGraphics, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            val renderY: Int = y + ITEM_HEIGHT / 2
            context.hLine(x + OFFSET, x + entryWidth - OFFSET * 2, renderY, if (hovered) 0x7FFFFFFF else 0x40FFFFFF)
            if (hovered) {
                context.drawMiddleAlignedString(Component.translatable("menu.flightassistant.flight_plan.add_waypoint"), x + entryWidth / 2, renderY - 4, 0xFFFFFFFF.toInt())
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
                states.add(order - 1, new)
            }
            this@EnrouteWaypointsListWidget.rebuildEntries()
            return true
        }
    }

    inner class WaypointEntry(private val state: EnrouteWaypointState, order: Int, x: Int, y: Int, width: Int) : AbstractEntry(x, y, width) {
        private val displayText: StringWidget = StringWidget(x + 5, y, width / 3, 9, Component.translatable("menu.flightassistant.flight_plan.enroute", order), font).alignLeft()
        private val fieldWidth: Int = width / 3 - 4
        private val xField: EditBox = EditBox(
            mc.font, x + width - fieldWidth * 2 - 8, y, fieldWidth, 15, null, Component.empty()
        )
        private val zField: EditBox = EditBox(
            mc.font, x + width - fieldWidth - 4, y, fieldWidth, 15, null, Component.empty()
        )
        private val altitudeField: EditBox = EditBox(
            mc.font, x + width + 15, this@EnrouteWaypointsListWidget.top, fieldWidth, 15, null, Component.empty()
        )

        private val speedButton: Button = Button.builder(
            Component.translatable("menu.flightassistant.autoflight.thrust.selected_speed")
        ) { state.thrustModeType = ButtonType.SelectedSpeed }
            .bounds(x + width + 15, this@EnrouteWaypointsListWidget.top + 30, fieldWidth, 15).build()
        private val verticalTargetButton: Button = Button.builder(
            Component.translatable("menu.flightassistant.autoflight.thrust.vertical_target")
        ) { state.thrustModeType = ButtonType.SelectedVerticalTarget }
            .bounds(x + width + fieldWidth + 20, this@EnrouteWaypointsListWidget.top + 30, fieldWidth, 15).build()
        private val speedField: EditBox = EditBox(
            mc.font, x + width + 15, this@EnrouteWaypointsListWidget.top + 50, fieldWidth, 15, null, Component.empty()
        )
        private val climbThrustField: EditBox = EditBox(
            mc.font, x + width + 15, this@EnrouteWaypointsListWidget.top + 50, fieldWidth, 15, null, Component.empty()
        )
        private val descendThrustField: EditBox = EditBox(
            mc.font, x + width + fieldWidth + 20, this@EnrouteWaypointsListWidget.top + 50, fieldWidth, 15, null, Component.empty()
        )

        init {
            xField.text = state.x?.toString() ?: ""
            xField.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.lateral.target_x"))
            xField.setTextPredicate {
                val i: Double? = it.toDoubleOrNull()
                it.isEmpty() || it == "-" || i != null
            }
            xField.setChangedListener { state.x = it.toDoubleOrNull() }

            zField.text = state.z?.toString() ?: ""
            zField.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.lateral.target_z"))
            zField.setTextPredicate {
                val i: Double? = it.toDoubleOrNull()
                it.isEmpty() || it == "-" || i != null
            }
            zField.setChangedListener { state.z = it.toDoubleOrNull() }

            altitudeField.text = state.altitude?.toString() ?: ""
            altitudeField.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.vertical.target_altitude"))
            altitudeField.setTextPredicate {
                val i: Double? = it.toDoubleOrNull()
                it.isEmpty() || it == "-" || i != null
            }
            altitudeField.setChangedListener { state.altitude = it.toDoubleOrNull() }

            speedField.text = state.thrustField1?.toString() ?: ""
            speedField.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.thrust.target_speed"))
            speedField.setTextPredicate {
                val i: Float? = it.toFloatOrNull()
                it.isEmpty() || i != null && i > 0.0f
            }
            speedField.setChangedListener { state.thrustField1 = it.toFloatOrNull() }

            climbThrustField.text = state.thrustField1?.toString() ?: ""
            climbThrustField.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.thrust.climb_thrust"))
            climbThrustField.setTextPredicate {
                val i: Float? = it.toFloatOrNull()
                it.isEmpty() || i != null && i in 0.0f..100.0f
            }
            climbThrustField.setChangedListener {
                val i: Float? = it.toFloatOrNull()
                if (i != null && i == 100.0f) {
                    climbThrustField.text = "99"
                }
                state.thrustField1 = i
            }

            descendThrustField.text = state.thrustField2?.toString() ?: ""
            descendThrustField.setPlaceholder(Component.translatable("menu.flightassistant.autoflight.thrust.descend_thrust"))
            descendThrustField.setTextPredicate {
                val i: Float? = it.toFloatOrNull()
                it.isEmpty() || i != null && i in 0.0f..100.0f
            }
            descendThrustField.setChangedListener {
                val i: Float? = it.toFloatOrNull()
                if (i != null && i == 100.0f) {
                    descendThrustField.text = "99"
                }
                state.thrustField2 = i
            }
        }

        override fun render(context: GuiGraphics, index: Int, y: Int, x: Int, entryWidth: Int, entryHeight: Int, mouseX: Int, mouseY: Int, hovered: Boolean, tickDelta: Float) {
            val renderY: Int = y + OFFSET

            displayText.y = renderY + 1
            displayText.render(context, mouseX, mouseY, tickDelta)

            xField.y = renderY - 2
            xField.render(context, mouseX, mouseY, tickDelta)
            zField.y = renderY - 2
            zField.render(context, mouseX, mouseY, tickDelta)

            if (isFocused) {
                speedButton.active = state.thrustModeType == ButtonType.SelectedVerticalTarget
                verticalTargetButton.active = state.thrustModeType == ButtonType.SelectedSpeed

                context.disableScissor()
                altitudeField.render(context, mouseX, mouseY, tickDelta)
                speedButton.render(context, mouseX, mouseY, tickDelta)
                verticalTargetButton.render(context, mouseX, mouseY, tickDelta)
                if (state.thrustModeType == ButtonType.SelectedSpeed) {
                    speedField.render(context, mouseX, mouseY, tickDelta)
                } else {
                    climbThrustField.render(context, mouseX, mouseY, tickDelta)
                    descendThrustField.render(context, mouseX, mouseY, tickDelta)
                }
                this@EnrouteWaypointsListWidget.enableScissor(context)
            }
        }

        override fun children(): MutableList<out Element> {
            val list = ArrayList<Element>()
            list.add(displayText)
            list.add(xField)
            list.add(zField)
            list.add(altitudeField)
            if (isFocused) {
                list.add(speedButton)
                list.add(verticalTargetButton)
                if (state.thrustModeType == ButtonType.SelectedSpeed) {
                    list.add(speedField)
                } else {
                    list.add(climbThrustField)
                    list.add(descendThrustField)
                }
            }
            return list
        }

        override fun selectableChildren(): MutableList<out Selectable> {
            val list = ArrayList<Selectable>()
            list.add(displayText)
            list.add(xField)
            list.add(zField)
            list.add(altitudeField)
            if (isFocused) {
                list.add(speedButton)
                list.add(verticalTargetButton)
                if (state.thrustModeType == ButtonType.SelectedSpeed) {
                    list.add(speedField)
                } else {
                    list.add(climbThrustField)
                    list.add(descendThrustField)
                }
            }
            return list
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
                        is AutopilotLogicComputer.VerticalTargetThrustMode -> mode.climbThrust * 100.0f
                        else -> throw UnsupportedOperationException()
                    }, when (val mode = waypoint.thrustMode) {
                        is AutopilotLogicComputer.SpeedThrustMode -> null
                        is AutopilotLogicComputer.VerticalTargetThrustMode -> mode.descendThrust * 100.0f
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
                            val climbThrust: Float = state.thrustField1!!.toFloat()
                            val descendThrust: Float = state.thrustField2!!.toFloat()
                            AutopilotLogicComputer.VerticalTargetThrustMode(climbThrust / 100.0f, descendThrust / 100.0f)
                        }

                        else -> throw UnsupportedOperationException()
                    }
                )
            )
        }

        load()
    }

    // TODO: Double? -> Int? (Int can do [-2b, +2b] wtf we don't need doubles)
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
                    is AutopilotLogicComputer.VerticalTargetThrustMode -> thrustMode.climbThrust != thrustField1?.div(100.0f) || thrustMode.descendThrust != thrustField2?.div(100.0f)
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
