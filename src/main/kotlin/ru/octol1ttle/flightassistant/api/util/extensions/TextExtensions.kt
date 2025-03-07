package ru.octol1ttle.flightassistant.api.util.extensions

import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text

fun MutableText.appendWithSeparation(other: Text) {
    if (this.siblings.isNotEmpty()) {
        this.append(" ")
    }
    this.append(other)
}

fun MutableText.setColor(color: Int): MutableText {
    this.setStyle(Style.EMPTY.withColor(color))
    return this
}
