package ru.octol1ttle.flightassistant

import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.api.computer.ComputerView
import ru.octol1ttle.flightassistant.api.util.FATickCounter
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.screen.FlightSetupScreen

object FAKeyBindings {
    internal val keyBindings: MutableList<KeyBinding> = ArrayList()

    private lateinit var toggleEnabled: KeyBinding

    private lateinit var openFlightSetup: KeyBinding

    private lateinit var autopilotDisconnect: KeyBinding
    private lateinit var manualPitchOverride: KeyBinding

    private lateinit var hideCurrentAlert: KeyBinding
    private lateinit var showHiddenAlert: KeyBinding

    private lateinit var setIdle: KeyBinding
    private lateinit var decreaseThrust: KeyBinding
    private lateinit var increaseThrust: KeyBinding
    private lateinit var setToga: KeyBinding

    fun setup() {
        toggleEnabled = addKeyBinding("toggle_enabled", -1)
        openFlightSetup = addKeyBinding("open_flight_setup", GLFW.GLFW_KEY_KP_ENTER)

        autopilotDisconnect = addKeyBinding("autopilot_disconnect", GLFW.GLFW_KEY_CAPS_LOCK)
        manualPitchOverride = addKeyBinding("manual_pitch_override", GLFW.GLFW_KEY_LEFT_ALT)

        hideCurrentAlert = addKeyBinding("hide_current_alert", GLFW.GLFW_KEY_KP_0)
        showHiddenAlert = addKeyBinding("show_hidden_alert", GLFW.GLFW_KEY_KP_DECIMAL)

        setIdle = addKeyBinding("set_idle", GLFW.GLFW_KEY_LEFT, "key.flightassistant.thrust")
        decreaseThrust = addKeyBinding("decrease_thrust", GLFW.GLFW_KEY_DOWN, "key.flightassistant.thrust")
        increaseThrust = addKeyBinding("increase_thrust", GLFW.GLFW_KEY_UP, "key.flightassistant.thrust")
        setToga = addKeyBinding("set_toga", GLFW.GLFW_KEY_RIGHT, "key.flightassistant.thrust")
    }

    private fun addKeyBinding(translationKey: String, code: Int, category: String = "key.flightassistant"): KeyBinding {
        val keyBinding = KeyBinding("${category}.${translationKey}", InputUtil.Type.KEYSYM, code, category)
        keyBindings.add(keyBinding)
        return keyBinding
    }

    fun checkPressed(computers: ComputerView) {
        while (toggleEnabled.wasPressed()) {
            FAConfig.global.modEnabled = !FAConfig.global.modEnabled
        }

        while (openFlightSetup.wasPressed()) {
            mc.execute {
                mc.setScreen(FlightSetupScreen())
            }
        }

        while (autopilotDisconnect.wasPressed()) {
            if (!computers.automations.autopilot && !computers.automations.autopilotAlert) {
                computers.automations.setFlightDirectors(false)
            }
            computers.automations.setAutoPilot(false, alert = false)
        }
        computers.pitch.manualOverride = manualPitchOverride.isPressed

        while (hideCurrentAlert.wasPressed()) {
            computers.alert.hideCurrentAlert()
        }
        while (showHiddenAlert.wasPressed()) {
            computers.alert.showHiddenAlert()
        }

        while (setIdle.wasPressed()) {
            computers.thrust.setTarget(0.0f)
        }
        while (setToga.wasPressed()) {
            computers.thrust.setTarget(1.0f)
        }
        while (decreaseThrust.wasPressed()) {
            computers.thrust.setTarget((computers.thrust.current - FATickCounter.timePassed / 3).coerceIn(-1.0f..1.0f))
        }
        while (increaseThrust.wasPressed()) {
            computers.thrust.setTarget((computers.thrust.current + FATickCounter.timePassed / 3).coerceIn(-1.0f..1.0f))
        }
    }

    fun isHoldingThrust(): Boolean {
        return setIdle.isPressed || setToga.isPressed
    }
}
