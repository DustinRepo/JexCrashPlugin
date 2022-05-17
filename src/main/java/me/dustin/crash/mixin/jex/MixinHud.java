package me.dustin.crash.mixin;

import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.mod.impl.render.hud.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Hud.class, remap = false)
public class MixinHud {

    @Inject(method = "getCategoryColor", at = @At("RETURN"), cancellable = true)
    public void getCategoryColor(Feature.Category category, CallbackInfoReturnable<Integer> cir) {
        if (category.name().equals("CRASH"))
            cir.setReturnValue(0xffff0000);
    }
}
