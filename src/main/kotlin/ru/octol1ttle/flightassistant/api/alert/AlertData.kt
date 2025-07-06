package ru.octol1ttle.flightassistant.api.alert

import net.minecraft.sounds.SoundEvent
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.config.FAConfig

class AlertData(val priority: Int, val soundEvent: SoundEvent, val repeat: RepeatType, val colorSupplier: () -> Int) {
    companion object {
        private fun soundEvent(name: String): SoundEvent {
            return SoundEvent.createVariableRangeEvent(FlightAssistant.id(name))
        }
        
        val FULL_STALL =
            AlertData(
                0,
                soundEvent("full_stall"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val APPROACHING_STALL =
            AlertData(
                100,
                soundEvent("approaching_stall"),
                RepeatType.REPEAT_FADE_IN_OUT,
            ) { FAConfig.display.cautionColor.rgb }
        val PULL_UP =
            AlertData(
                200,
                soundEvent("pull_up"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val SINK_RATE =
            AlertData(
                300,
                soundEvent("sink_rate"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val TERRAIN_AHEAD =
            AlertData(
                400,
                soundEvent("terrain_ahead"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val FORCE_AUTOPILOT_OFF =
            AlertData(
                500,
                soundEvent("autopilot_off"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.warningColor.rgb }
        val PLAYER_AUTOPILOT_OFF =
            AlertData(
                500,
                soundEvent("autopilot_off"),
                RepeatType.NO_REPEAT
            ) { FAConfig.display.warningColor.rgb }
        val MASTER_WARNING =
            AlertData(
                600,
                soundEvent("master_warning"),
                RepeatType.REPEAT_FADE_OUT
            ) { FAConfig.display.warningColor.rgb }
        val THRUST_LOCKED =
            AlertData(
                700,
                soundEvent("thrust_locked"),
                RepeatType.REPEAT_CONSTANT_VOLUME
            ) { FAConfig.display.cautionColor.rgb }
        val MASTER_CAUTION =
            AlertData(
                800,
                soundEvent("master_caution"),
                RepeatType.NO_REPEAT
            ) { FAConfig.display.cautionColor.rgb }
    }

    enum class RepeatType {
        REPEAT_FADE_IN_OUT,
        REPEAT_FADE_OUT,
        REPEAT_CONSTANT_VOLUME,
        NO_REPEAT
    }
}
