package ru.octol1ttle.flightassistant.screen.components

import com.google.common.base.Predicates
import java.util.function.Consumer
import java.util.function.Predicate
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.util.extensions.font

class TypeStrictEditBox<T>(x: Int, y: Int, width: Int, height: Int, initialValue: T, onValueChange: Consumer<T>, convertFunction: (String) -> T?, filter: Predicate<T> = Predicates.alwaysTrue<T>()) : EditBox(font, x, y, width, height, Component.empty()) {
    private val convertFunction: (String) -> T? = convertFunction
    private val filter: Predicate<T> = filter

    init {
        this.value = initialValue.toString()
//? if <26.1 {
        this.setFilter {
            val value: T? = convertFunction.invoke(it)
            value != null && filter.test(value)
        }
//?}
        this.setResponder { onValueChange.accept(convertFunction.invoke(it)!!) }
    }

//? if >=26.1 {
    /*override fun insertText(textToWrite: String) {
        val value: T? = convertFunction.invoke(this.value + textToWrite)
        if (value != null && filter.test(value)) {
            super.insertText(textToWrite)
        }
    }
*///?}
}