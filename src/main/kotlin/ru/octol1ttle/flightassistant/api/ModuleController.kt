package ru.octol1ttle.flightassistant.api

import net.minecraft.resources.ResourceLocation

interface ModuleController<T> : ModuleView<T> {
    fun register(identifier: ResourceLocation, module: T)
    fun setEnabled(identifier: ResourceLocation, enabled: Boolean): Boolean
    fun toggleEnabled(identifier: ResourceLocation): Boolean {
        return setEnabled(identifier, !this.isEnabled(identifier))
    }
}
