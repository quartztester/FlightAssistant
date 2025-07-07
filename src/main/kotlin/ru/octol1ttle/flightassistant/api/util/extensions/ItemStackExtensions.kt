package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.world.item.ElytraItem
import net.minecraft.world.item.ItemStack

fun ItemStack?.canUse(): Boolean {
//? if >=1.21.2 {
    /*return this?.willBreakNextUse() == false
*///?} else
    return this != null && ElytraItem.isFlyEnabled(this)
}
