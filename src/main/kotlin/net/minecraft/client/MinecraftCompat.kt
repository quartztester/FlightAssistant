package net.minecraft.client

import net.minecraft.client.gui.screens.Screen

//? if >=26.2 {
fun Minecraft.setScreen(screen: Screen?) {
    this.gui.setScreen(screen)
}
//?}
