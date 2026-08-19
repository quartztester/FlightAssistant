package ru.octol1ttle.flightassistant.mixin.level_renderer;

//? if >=26.2 {
/*import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LevelRenderer.class)
abstract class LevelRendererMixin26 {
    @org.spongepowered.asm.mixin.injection.Inject(method = "render", at = @org.spongepowered.asm.mixin.injection.At("HEAD"))
    private void onStartRender(com.mojang.blaze3d.resource.GraphicsResourceAllocator graphicsResourceAllocator, net.minecraft.client.DeltaTracker deltaTracker, boolean renderBlockOutline, net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState, org.joml.Matrix4fc frustumMatrix, com.mojang.blaze3d.buffers.GpuBufferSlice fogBuffer, org.joml.Vector4f fogColor, boolean renderSky, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        ru.octol1ttle.flightassistant.api.util.event.LevelRenderCallback.EVENT.invoker().onStartRenderLevel(deltaTracker.getGameTimeDeltaPartialTick(true), net.minecraft.client.Minecraft.getInstance().gameRenderer.mainCamera(), cameraRenderState.projectionMatrix, cameraRenderState.viewRotationMatrix.get3x3(new org.joml.Matrix3f()));
    }
}
*///?}
