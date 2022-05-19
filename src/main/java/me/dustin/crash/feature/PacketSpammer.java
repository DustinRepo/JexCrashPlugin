package me.dustin.crash.feature;

import me.dustin.crash.CrashPlugin;
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
import net.minecraft.network.packet.c2s.play.KeepAliveC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

public class PacketSpammer extends Feature {

    @Op(name = "Packet Count", min = 1, max = 100, inc = 10)
    public int packetCount = 15;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    public PacketSpammer() {
        super(CrashPlugin.CRASH, "Spams various packets to the server. Likely to get you kicked instantly. (By BleachDrinker420)");
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        for (int i = 0; i < packetCount; i++) {
            NetworkHelper.INSTANCE.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(Math.random() >= 0.5));
            NetworkHelper.INSTANCE.sendPacket(new KeepAliveC2SPacket((int) (Math.random() * 8)));
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.POST));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));
}
