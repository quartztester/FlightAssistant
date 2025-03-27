package ru.octol1ttle.flightassistant.screen.flightplan

import net.minecraft.client.gui.Element
import net.minecraft.client.gui.ParentElement
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.impl.computer.ComputerHost
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class FlightPlanScreen : FABaseScreen(Text.translatable("menu.flightassistant.flight_plan")) {
    private lateinit var doneButton: ButtonWidget
    private lateinit var saveButton: ButtonWidget
    private lateinit var discardChangesButton: ButtonWidget

    private val states: MutableList<FlightPlanState> = ArrayList()
    private var loaded: Boolean = false

    override fun init() {
        super.init()

        this.addDrawableChild(TextWidget(this.width / 2, 10, this.width / 2, 9, this.title, this.textRenderer))

        addWaypointsList()

        doneButton = ButtonWidget.builder(ScreenTexts.DONE) { _: ButtonWidget? ->
            this.close()
        }.position(this.width - 100, this.height - 30).width(80).build()
        saveButton = ButtonWidget.builder(Text.translatable("menu.flightassistant.flight_plan.save")) { _: ButtonWidget? ->
            for (state: FlightPlanState in states) {
                state.save()
            }
        }.position(this.width - 190, this.height - 30).width(80).build()
        discardChangesButton = ButtonWidget.builder(Text.translatable("menu.flightassistant.flight_plan.discard_changes")) { _: ButtonWidget? ->
            for (state: FlightPlanState in states) {
                state.load()
            }
        }.position(this.width - 300, this.height - 30).width(100).build()

        this.addDrawableChild(doneButton)
        this.addDrawableChild(saveButton)
        this.addDrawableChild(discardChangesButton)
    }

    override fun tick() {
        super.tick()
        doneButton.active = states.all { !it.needsSaving() }
        saveButton.active = !doneButton.active && states.all { it.canSave() }
        discardChangesButton.active = !doneButton.active
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        for (element: Element in this.children()) {
            if (element.mouseClicked(mouseX, mouseY, button)) {
                val focused: Element? = focused
                if (element != focused && focused is ParentElement) {
                    focused.focused = null
                }
                this.focused = element
                if (button == 0) {
                    this.isDragging = true
                }

                return true
            }
        }

        return false
    }

    private fun addWaypointsList() {
        val width: Int = this.width / 2 - EnrouteWaypointsListWidget.OFFSET
        val left: Int = EnrouteWaypointsListWidget.OFFSET * 2
        val top: Int = left + EnrouteWaypointsListWidget.ITEM_HEIGHT + EnrouteWaypointsListWidget.OFFSET
        val bottom: Int = this.height - EnrouteWaypointsListWidget.ITEM_HEIGHT - 10
        val height: Int = bottom - top

        val computers: ComputerView = ComputerHost

        val departureWaypointWidget = DepartureWaypointWidget(computers, left, left, width, EnrouteWaypointsListWidget.ITEM_HEIGHT)
        //val arrivalWaypointWidget = ArrivalWaypointWidget(computers, left, this.height - WaypointsListWidget.ITEM_HEIGHT - 5, width, WaypointsListWidget.ITEM_HEIGHT)
        val waypointsListWidget = EnrouteWaypointsListWidget(computers, width, height, top, bottom, 5)
        //? if >=1.21 {
        /*waypointsListWidget.x = left
*///?} else
        waypointsListWidget.setLeftPos(left)

        if (!loaded) {
            departureWaypointWidget.load()
            //arrivalWaypointWidget.load()
            waypointsListWidget.load()
        }
        loaded = true

        states.add(this.addDrawableChild(departureWaypointWidget))
        //states.add(this.addDrawableChild(arrivalWaypointWidget))
        states.add(this.addDrawableChild(waypointsListWidget))
    }
}
