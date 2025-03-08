package ru.octol1ttle.flightassistant.impl.display

import java.util.Objects
import kotlin.math.roundToInt
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import ru.octol1ttle.flightassistant.FAKeyBindings
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.display.Display
import ru.octol1ttle.flightassistant.api.display.HudFrame
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.api.util.extensions.*
import ru.octol1ttle.flightassistant.api.util.furtherFromZero
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.autoflight.AutopilotLogicComputer
import ru.octol1ttle.flightassistant.impl.computer.autoflight.ThrustComputer.Companion.TOGA_THRESHOLD

class AutomationModesDisplay(computers: ComputerView) : Display(computers) {
    private val thrustDisplay: ModeDisplay = ModeDisplay(1)
    private val pitchDisplay: ModeDisplay = ModeDisplay(2)
    private val headingDisplay: ModeDisplay = ModeDisplay(3)
    private val automationStatusDisplay: ModeDisplay = ModeDisplay(5)

    override fun allowedByConfig(): Boolean {
        return FAConfig.display.showAutomationModes
    }

    override fun render(drawContext: DrawContext) {
        renderThrustMode(drawContext)
        renderPitchMode(drawContext)
        drawInput(drawContext, headingDisplay, computers.heading.activeInput)
        renderAutomaticsMode(drawContext)
    }

    private fun renderThrustMode(drawContext: DrawContext) {
        val thrustUnusable: Boolean = computers.thrust.noThrustSource || computers.thrust.reverseUnsupported

        val input: ControlInput? = computers.thrust.activeInput
        if (input != null) {
            if (FAKeyBindings.isHoldingThrust()) {
                thrustDisplay.render(drawContext, Text.translatable("mode.flightassistant.thrust.override").setColor(cautionColor), false, cautionColor)
            } else {
                thrustDisplay.render(
                    drawContext, input.text, input.active,
                    if (thrustUnusable || input.active && input.priority < ControlInput.Priority.NORMAL) cautionColor else null
                )
            }
            return
        }

        val thrustValueText: MutableText = Text.literal(furtherFromZero(computers.thrust.current * 100).toString() + "%").setColor(primaryColor)
        if (computers.thrust.thrustLocked) {
            thrustDisplay.render(
                drawContext,
                if (computers.thrust.current > TOGA_THRESHOLD) Text.translatable("mode.flightassistant.thrust.locked_toga").setColor(primaryColor)
                else Text.translatable("mode.flightassistant.thrust.locked", thrustValueText).setColor(primaryColor),
                false,
                if (FATickCounter.totalTicks % 20 >= 10) cautionColor else emptyColor
            )

            return
        }

        if (computers.thrust.current != 0.0f) {
            thrustDisplay.render(
                drawContext,
                if (computers.thrust.current > TOGA_THRESHOLD) Text.translatable("mode.flightassistant.thrust.manual_toga")
                else Text.translatable("mode.flightassistant.thrust.manual", thrustValueText),
                false
            )
            return
        }

        thrustDisplay.render(drawContext, null, true)
    }

    private fun renderPitchMode(drawContext: DrawContext) {
        if (computers.pitch.manualOverride) {
            pitchDisplay.render(drawContext, Text.translatable("mode.flightassistant.vertical.override").setColor(cautionColor), false, cautionColor)
            return
        }
        drawInput(drawContext, pitchDisplay, computers.pitch.activeInput)
    }

    private fun drawInput(drawContext: DrawContext, display: ModeDisplay, input: ControlInput?) {
        if (input != null) {
            display.render(drawContext, input.text, input.active, if (input.active && input.priority < ControlInput.Priority.NORMAL) cautionColor else null)
        } else {
            display.render(drawContext, null, true)
        }
    }

    private fun renderAutomaticsMode(drawContext: DrawContext) {
        val text: MutableText = Text.empty()
        if (computers.automations.flightDirectors) {
            text.appendWithSeparation(Text.translatable("short.flightassistant.flight_directors"))
        }
        if (computers.automations.autoThrust) {
            val autoThrustText: MutableText = Text.translatable("short.flightassistant.auto_thrust")
            text.appendWithSeparation(
                if (computers.thrust.activeInput?.identifier == AutopilotLogicComputer.ID) autoThrustText
                else autoThrustText.setColor(advisoryColor)
            )
        }
        if (computers.automations.autopilot) {
            text.appendWithSeparation(Text.translatable("short.flightassistant.autopilot"))
        }

        automationStatusDisplay.render(
            drawContext,
            if (text.siblings.isNotEmpty()) text else null,
            true,
            if (computers.automations.autopilotAlert) warningColor
            else if (computers.automations.autoThrustAlert) cautionColor
            else null
        )
    }

    override fun renderFaulted(drawContext: DrawContext) {
        with(drawContext) {
            val x: Int = centerX
            val y: Int = HudFrame.top - 9

            drawMiddleAlignedText(Text.translatable("short.flightassistant.automation_modes"), x, y, warningColor)
        }
    }

    companion object {
        val ID: Identifier = FlightAssistant.id("automation_modes")
        private const val TOTAL_MODES: Float = 5.0f
    }

    class ModeDisplay(private val order: Int) {
        private var lastText: Text? = null
        private var textChangedAt: Int = 0

        fun render(drawContext: DrawContext, text: Text?, active: Boolean = true, borderColor: Int? = null) {
            val farLeft: Int = HudFrame.left - 35
            val farRight: Int = HudFrame.right + 35
            val farWidth: Int = farRight - farLeft

            val singleWidth: Int = ((farWidth - (TOTAL_MODES - 1)) / TOTAL_MODES).roundToInt()
            val leftX: Int = farLeft + (singleWidth + 1) * (order - 1)
            val rightX: Int = if (order == TOTAL_MODES.toInt()) farRight + 1 else leftX + singleWidth

            val y: Int = HudFrame.top - 10

            if (active && !Objects.equals(text, lastText)) {
                textChangedAt = FATickCounter.totalTicks
                lastText = text
            }

            if (text != null) {
                drawContext.drawMiddleAlignedText(text, (leftX + rightX) / 2, y, if (active) primaryColor else secondaryColor)
            }
            if (borderColor != null || FATickCounter.totalTicks <= textChangedAt + (if (text == null) 60 else 100)) {
                drawContext.drawBorder(leftX, y - 2, rightX - leftX, 11, borderColor ?: secondaryColor)
            }
        }
    }
}
