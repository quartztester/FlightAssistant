package ru.octol1ttle.flightassistant.impl.computer.safety

import kotlin.math.max
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import ru.octol1ttle.flightassistant.FlightAssistant
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput
import ru.octol1ttle.flightassistant.api.autoflight.FlightController
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiter
import ru.octol1ttle.flightassistant.api.autoflight.pitch.PitchLimiterRegistrationCallback
import ru.octol1ttle.flightassistant.api.autoflight.thrust.ThrustControllerRegistrationCallback
import ru.octol1ttle.flightassistant.api.computer.Computer
import ru.octol1ttle.flightassistant.api.computer.ComputerBus
import ru.octol1ttle.flightassistant.api.util.inverseMin
import ru.octol1ttle.flightassistant.api.util.requireIn
import ru.octol1ttle.flightassistant.config.FAConfig
import ru.octol1ttle.flightassistant.impl.computer.data.AirDataComputer

class GroundProximityComputer(computers: ComputerBus) : Computer(computers), PitchLimiter, FlightController {
    private var groundImpactTime: Double = Double.MAX_VALUE
    var groundImpactStatus: Status = Status.SAFE
        private set
    private var obstacleImpactTime: Double = Double.MAX_VALUE
    var obstacleImpactStatus: Status = Status.SAFE
        private set

    override fun subscribeToEvents() {
        ThrustControllerRegistrationCallback.EVENT.register { it.accept(this) }
        PitchLimiterRegistrationCallback.EVENT.register { it.accept(this) }
        PitchControllerRegistrationCallback.EVENT.register { it.accept(this) }
    }

    override fun tick() {
        val data: AirDataComputer = computers.data
        if (!data.flying || data.player.isInWater) {
            groundImpactStatus = Status.SAFE
            obstacleImpactStatus = Status.SAFE
            return
        }

        val anyBlocksAbove: Boolean = data.level.getHeight(Heightmap.Types.MOTION_BLOCKING, data.player.blockX, data.player.blockZ) > data.player.y
        val clearThreshold: Double = if (anyBlocksAbove) 7.5 else 10.0
        val cautionThreshold: Double = if (anyBlocksAbove) 3.0 else 7.5
        val warningThreshold: Double = if (anyBlocksAbove) 1.5 else 3.0
        val recoverThreshold = 0.75

        groundImpactTime = computeGroundImpactTime(data).requireIn(0.0..Double.MAX_VALUE)
        groundImpactStatus =
            if (data.fallDistanceSafe) {
                Status.SAFE
            } else if (groundImpactStatus == Status.SAFE && (data.velocityPerSecond.y > -10 || groundImpactTime > cautionThreshold)) {
                Status.SAFE
            } else if (data.velocityPerSecond.y > -7.5 || groundImpactTime > clearThreshold) {
                Status.SAFE
            } else if (groundImpactStatus >= Status.CAUTION && groundImpactTime > warningThreshold) {
                Status.CAUTION
            } else if (groundImpactStatus >= Status.WARNING && groundImpactTime > recoverThreshold) {
                Status.WARNING
            } else {
                Status.RECOVER
            }

        obstacleImpactTime = computeObstacleImpactTime(data, clearThreshold).requireIn(0.0..Double.MAX_VALUE)

        val damageOnCollision: Double = data.velocity.horizontalDistance() * 10 - 3
        obstacleImpactStatus =
            if (data.isInvulnerableTo(data.player.damageSources().flyIntoWall())) {
                Status.SAFE
            } else if (obstacleImpactStatus == Status.SAFE && (damageOnCollision < data.player.health * 0.5f || obstacleImpactTime > groundImpactTime * 1.1f || obstacleImpactTime > cautionThreshold)) {
                Status.SAFE
            } else if (damageOnCollision < data.player.health * 0.25f || obstacleImpactTime > groundImpactTime * 1.5f || obstacleImpactTime > clearThreshold) {
                Status.SAFE
            } else if (obstacleImpactStatus >= Status.CAUTION && obstacleImpactTime > warningThreshold) {
                Status.CAUTION
            } else if (obstacleImpactStatus >= Status.WARNING && obstacleImpactTime > recoverThreshold) {
                Status.WARNING
            } else {
                Status.RECOVER
            }
    }

    private fun computeGroundImpactTime(data: AirDataComputer): Double {
        if (data.velocity.y >= 0.0) {
            return Double.MAX_VALUE
        }

        val groundLevel: Double? = data.groundY
        val impactLevel: Double =
            if (groundLevel == null || groundLevel == Double.MAX_VALUE) data.voidY.toDouble()
            else groundLevel
        return max(0.0, data.altitude - impactLevel) / -data.velocityPerSecond.y
    }

    private fun computeObstacleImpactTime(data: AirDataComputer, lookAheadTime: Double): Double {
        val end: Vec3 = data.position.add(data.forwardVelocityPerSecond.multiply(lookAheadTime, 0.0, lookAheadTime))
        val result: BlockHitResult = data.level.clip(
            ClipContext(
                data.position,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.ANY,
                data.player
            )
        )
        if (result.type != HitResult.Type.BLOCK) {
            return Double.MAX_VALUE
        }

        return data.position.distanceTo(result.location) / data.forwardVelocityPerSecond.length()
    }

    override fun getMinimumPitch(): ControlInput? {
        if (groundImpactStatus <= Status.WARNING && FAConfig.safety.sinkRateLimitPitch || obstacleImpactStatus <= Status.WARNING && FAConfig.safety.obstacleLimitPitch) {
            return ControlInput(
                computers.data.pitch.coerceAtMost(computers.thrust.getAltitudeHoldPitch()),
                ControlInput.Priority.HIGH,
                Component.translatable("mode.flightassistant.vertical.terrain_protection")
            )
        }

        return null
    }

    override fun getThrustInput(): ControlInput? {
        if (groundImpactStatus <= Status.CAUTION && FAConfig.safety.sinkRateAutoThrust || obstacleImpactStatus <= Status.CAUTION && FAConfig.safety.obstacleAutoThrust) {
            if (computers.data.pitch <= computers.thrust.getAltitudeHoldPitch()) {
                return ControlInput(
                    0.0f,
                    ControlInput.Priority.HIGH,
                    Component.translatable("mode.flightassistant.thrust.idle"),
                    active = groundImpactStatus <= Status.WARNING && FAConfig.safety.sinkRateAutoThrust || obstacleImpactStatus <= Status.WARNING && FAConfig.safety.obstacleAutoThrust
                )
            }
        }

        return null
    }

    override fun getPitchInput(): ControlInput? {
        if (groundImpactStatus <= Status.WARNING && FAConfig.safety.sinkRateAutoPitch || obstacleImpactStatus <= Status.WARNING && FAConfig.safety.obstacleAutoPitch) {
            val deltaTimeMultiplier: Double = inverseMin(groundImpactTime, obstacleImpactTime) ?: return null
            return ControlInput(
                90.0f,
                ControlInput.Priority.HIGH,
                Component.translatable("mode.flightassistant.vertical.terrain_escape"),
                deltaTimeMultiplier.toFloat(),
                active = groundImpactStatus == Status.RECOVER && FAConfig.safety.sinkRateAutoPitch || obstacleImpactStatus == Status.RECOVER && FAConfig.safety.obstacleAutoPitch
            )
        }

        return null
    }

    override fun reset() {
        groundImpactTime = Double.MAX_VALUE
        groundImpactStatus = Status.SAFE
        obstacleImpactTime = Double.MAX_VALUE
        obstacleImpactStatus = Status.SAFE
    }

    enum class Status {
        RECOVER,
        WARNING,
        CAUTION,
        SAFE
    }

    companion object {
        val ID: ResourceLocation = FlightAssistant.id("ground_proximity")
    }
}
