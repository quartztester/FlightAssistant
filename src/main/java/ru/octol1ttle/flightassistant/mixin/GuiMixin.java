package ru.octol1ttle.flightassistant.mixin;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import ru.octol1ttle.flightassistant.api.util.event.FixedGuiRenderCallback;

@Mixin(Gui.class)
abstract class GuiMixin {
//? if fabric {
//? if >=1.21 {
@com.llamalad7.mixinextras.injector.ModifyReceiver(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/LayeredDraw;add(Lnet/minecraft/client/gui/LayeredDraw$Layer;)Lnet/minecraft/client/gui/LayeredDraw;", ordinal = 2))
public net.minecraft.client.gui.LayeredDraw render(net.minecraft.client.gui.LayeredDraw instance, net.minecraft.client.gui.LayeredDraw.Layer layer) {
    return instance.add((guiGraphics, deltaTracker) -> FixedGuiRenderCallback.EVENT.invoker().onRenderGui(
            guiGraphics,
//? if >=1.21.5 {
            /*deltaTracker.getGameTimeDeltaPartialTick(true)
             *///?} else
            deltaTracker.getGameTimeDeltaPartialTick(true)
        ));
    }
    //?} else {
    /*@org.spongepowered.asm.mixin.injection.Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;getCurrentGameMode()Lnet/minecraft/world/GameMode;", ordinal = 0))
    private void render(net.minecraft.client.gui.DrawContext guiGraphics, float tickDelta, org.spongepowered.asm.mixin.injection.callback.CallbackInfo callbackInfo) {
        FixedHudRenderCallback.EVENT.invoker().onRenderHud(guiGraphics, tickDelta);
    }
*///?}
//?}
}
