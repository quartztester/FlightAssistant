package ru.octol1ttle.flightassistant.screen.components

import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.font

class SmartStringWidget(x: Int, y: Int, component: Component) : StringWidget(x, y, font.width(component), font.lineHeight, component, font) {
    init {
        this.alignLeft()
    }

//? if >=1.21.9 {
    /*fun alignLeft(): SmartStringWidget {
        return this
    }

    fun alignCenter(): SmartStringWidget {
        this.x -= this.width / 2
        return this
    }

    fun alignRight(): SmartStringWidget {
        this.x -= this.width
        return this
    }
*///?}
}