package ru.octol1ttle.flightassistant.api.util.extensions

import com.mojang.blaze3d.vertex.PoseStack
import java.awt.Color
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FormattedText
import ru.octol1ttle.flightassistant.FlightAssistant.mc
import ru.octol1ttle.flightassistant.config.FAConfig

internal val font: Font = mc.font

val lineHeight: Int
    get() = font.lineHeight

val GuiGraphics.halfWidth: Float
    get() = guiWidth() * 0.5f

val GuiGraphics.centerXF: Float
    get() = halfWidth

val GuiGraphics.centerX: Int
    get() = centerXF.toInt()

val GuiGraphics.centerYF: Float
    get() = guiHeight() * 0.5f

val GuiGraphics.centerY: Int
    get() = centerYF.toInt()

const val emptyColor: Int = 0

val primaryColor: Int
    get() = FAConfig.display.primaryColor.rgb

val secondaryColor: Int
    get() = FAConfig.display.secondaryColor.rgb

val advisoryColor: Int
    get() = FAConfig.display.advisoryColor.rgb

val cautionColor: Int
    get() = FAConfig.display.cautionColor.rgb

val warningColor: Int
    get() = FAConfig.display.warningColor.rgb

/**
 * Translates this graphics' pose and then scales it.
 */
fun GuiGraphics.fusedTranslateScale(x: Float, y: Float, scale: Float) {
    pose().translate(x, y, 0.0f)
    pose().scale(scale, scale, 1.0f)
}

fun GuiGraphics.hLineDashed(
    x1: Int, x2: Int, y: Int,
    dashCount: Int, color: Int
) {
    val width: Int = x2 - x1
    val segmentCount: Int = dashCount * 2 - 1
    val dashSize: Int = width / segmentCount
    for (i in 0 until segmentCount) {
        if (i % 2 != 0) {
            continue
        }
        val dx1: Int = i * dashSize + x1
        val dx2: Int = if (i == segmentCount - 1) x2 else ((i + 1) * dashSize) + x1
        hLine(dx1, dx2, y, color)
    }
}

fun textWidth(text: String): Int {
    return font.width(text)
}

fun GuiGraphics.drawString(text: String, x: Int, y: Int, color: Int) {
    drawString(font, text, x, y, color, false)
}

fun GuiGraphics.drawRightAlignedString(text: String, x: Int, y: Int, color: Int) {
    drawString(font, text, x - font.width(text), y, color, false)
}

fun GuiGraphics.drawMiddleAlignedString(text: String, x: Int, y: Int, color: Int) {
    drawString(font, text, x - font.width(text) / 2 + 1, y, color, false)
}

fun textWidth(formattedText: FormattedText): Int {
    return font.width(formattedText)
}

fun GuiGraphics.drawString(text: Component, x: Int, y: Int, color: Int): Int {
    drawString(font, text, x, y, color, false)
    return 1
}

private fun getContrasting(original: Int): Int {
    val red: Int = original shr 16 and 255
    val green: Int = original shr 8 and 255
    val blue: Int = original shr 0 and 255
    val luma: Double = (0.299 * red + 0.587 * green + 0.114 * blue) / 255.0
    return if (luma > 0.5) Color.BLACK.rgb else Color.WHITE.rgb
}

fun GuiGraphics.drawRightAlignedString(text: Component, x: Int, y: Int, color: Int) {
    drawString(font, text, x - textWidth(text), y, color, false)
}

fun GuiGraphics.drawMiddleAlignedString(text: Component, x: Int, y: Int, color: Int) {
    drawString(font, text, x - textWidth(text) / 2 + 1, y, color, false)
}

fun GuiGraphics.drawHighlightedCenteredText(text: Component, x: Int, y: Int, color: Int, highlight: Boolean) {
    pose().pushPose()

    if (highlight) {
        val halfWidth: Int = textWidth(text) / 2
        fill(x - halfWidth - 1, y - 1, x + halfWidth + 2, y + 8, color)
        pose().translate(0, 0, 100)
        drawMiddleAlignedString(text, x, y, getContrasting(color))
    } else {
        drawMiddleAlignedString(text, x, y, color)
    }

    pose().popPose()
}

fun PoseStack.translate(x: Int, y: Int, z: Int) {
    translate(x.toFloat(), y.toFloat(), z.toFloat())
}
