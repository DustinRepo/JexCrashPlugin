package me.dustin.crash.mixin.minecraft;

import me.dustin.crash.event.EventWriteLoginHelloPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoginHelloC2SPacket.class)
public class MixinLoginHelloC2SPacket {

    @Inject(method = "write", at = @At("HEAD"), cancellable = true)
    public void writePacket(PacketByteBuf buf, CallbackInfo ci) {
        EventWriteLoginHelloPacket eventWriteLoginHelloPacket = new EventWriteLoginHelloPacket(buf).run();
        if (eventWriteLoginHelloPacket.isCancelled())
            ci.cancel();
    }

}
