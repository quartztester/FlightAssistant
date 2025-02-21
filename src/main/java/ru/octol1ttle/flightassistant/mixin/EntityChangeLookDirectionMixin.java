package ru.octol1ttle.flightassistant.mixin;

import dev.architectury.platform.Platform;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import ru.octol1ttle.flightassistant.MixinHandlerKt;
import ru.octol1ttle.flightassistant.api.autoflight.ControlInput;
import ru.octol1ttle.flightassistant.api.util.event.ChangeLookDirectionEvents;

@Mixin(Entity.class)
abstract class EntityChangeLookDirectionMixin {
    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 0)
    private float overridePitchChange(float pitchDelta) {
        List<ControlInput> list = new ArrayList<>();
        ChangeLookDirectionEvents.PITCH.invoker().onChangeLookDirection(pitchDelta, list);
        return Objects.requireNonNullElse(MixinHandlerKt.onEntityChangePitch(list), pitchDelta);
    }

    @ModifyVariable(method = "changeLookDirection", at = @At("STORE"), ordinal = 1)
    private float overrideHeadingChange(float headingDelta) {
        if (Platform.isModLoaded("do_a_barrel_roll") && Math.abs(headingDelta) >= 360.0f) {
            headingDelta %= 360.0f;
        }
        List<ControlInput> list = new ArrayList<>();
        ChangeLookDirectionEvents.HEADING.invoker().onChangeLookDirection(headingDelta, list);
        return Objects.requireNonNullElse(MixinHandlerKt.onEntityChangeHeading(list), headingDelta);
    }
}
