package ru.octol1ttle.flightassistant.screen

import kotlin.jvm.optionals.getOrNull
import net.minecraft.client.gui.*
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder

abstract class AbstractParentWidget : AbstractParentElement(), Drawable, Selectable {
    private var hoveredElement: Element? = null
    var forceFocused: Boolean = false

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        for (child: Element in children()) {
            if (child is Drawable) {
                child.render(context, mouseX, mouseY, delta)
            }
        }
        this.hoveredElement = this.hoveredElement(mouseX.toDouble(), mouseY.toDouble()).getOrNull()
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        for (child: Element in children()) {
            if (child.isMouseOver(mouseX, mouseY)) {
                return true
            }
        }

        return super.isMouseOver(mouseX, mouseY)
    }

    override fun appendNarrations(builder: NarrationMessageBuilder?) {
    }

    override fun getType(): Selectable.SelectionType {
        return if (this.isFocused) {
            Selectable.SelectionType.FOCUSED
        } else {
            if (this.hoveredElement != null) Selectable.SelectionType.HOVERED else Selectable.SelectionType.NONE
        }
    }

    override fun isFocused(): Boolean {
        return forceFocused || super.isFocused()
    }
}
