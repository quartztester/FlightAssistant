package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.world.entity.LivingEntity

val LivingEntity.fallFlying: Boolean
    get() {
//? if >=1.21.2 {
        /*return this.isGliding
*///?} else
        return this.isFallFlying
    }
