package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.SharedConstants
import net.minecraft.world.phys.Vec3

fun Vec3.perSecond(): Vec3 {
    return this.scale(SharedConstants.TICKS_PER_SECOND.toDouble())
}