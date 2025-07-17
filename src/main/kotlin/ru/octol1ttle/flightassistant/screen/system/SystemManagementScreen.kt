package ru.octol1ttle.flightassistant.screen.system

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.StringWidget
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import ru.octol1ttle.flightassistant.api.ModuleController
import ru.octol1ttle.flightassistant.screen.FABaseScreen

class SystemManagementScreen(title: Component, private val baseKey: String, private val controller: ModuleController<*>) : FABaseScreen(title) {
    override fun init() {
        super.init()

        this.addRenderableWidget(StringWidget(0, 7, this.width, 9, this.title, this.font))

        val top = 20
        val bottom: Int = this.height - 40
        val height: Int = bottom - top

        this.addRenderableWidget(SystemManagementList(this.width, height, top, bottom, 0, baseKey, controller))

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE) { _: Button? ->
            this.onClose()
        }.pos(this.width - 90, this.height - 30).width(80).build())
    }
}
