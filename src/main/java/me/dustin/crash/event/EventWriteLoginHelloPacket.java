package me.dustin.crash.event;

import me.dustin.events.core.Event;
import net.minecraft.network.PacketByteBuf;

public class EventWriteLoginHelloPacket extends Event {

    private final PacketByteBuf packetByteBuf;

    public EventWriteLoginHelloPacket(PacketByteBuf packetByteBuf) {
        this.packetByteBuf = packetByteBuf;
    }

    public PacketByteBuf getPacketByteBuf() {
        return packetByteBuf;
    }
}
