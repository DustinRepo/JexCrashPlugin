package me.dustin.crash.mixin.impl;

import me.dustin.crash.mixin.interf.IWorldClient;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientWorld.class)
public class MixinClientWorld implements IWorldClient {
    @Shadow @Final private PendingUpdateManager pendingUpdateManager;

    @Override
    public PendingUpdateManager getPendingUpdateManager() {
        return this.pendingUpdateManager;
    }
}
