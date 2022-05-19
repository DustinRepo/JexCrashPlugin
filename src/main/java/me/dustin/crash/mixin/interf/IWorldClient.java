package me.dustin.crash.mixin.interf;

import net.minecraft.client.network.PendingUpdateManager;

public interface IWorldClient {
    PendingUpdateManager getPendingUpdateManager();
}
